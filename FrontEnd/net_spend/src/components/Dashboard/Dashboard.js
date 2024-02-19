import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import SpendAnalyzer from "./SpendAnalyzer";
import QuickLinks from "../HomePage/QuickLinks";
import PersonalizedOffers from "../HomePage/PersonalizedOffers";
import { getCurrentUser } from "../../Services/authService";
import bankingService from "../../Services/bankingService";
import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import "./Dashboard.css";

function Dashboard() {
  const [user, setUser] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [balance, setBalance] = useState(0);
  const [showBalance, setShowBalance] = useState(false);

  useEffect(() => {
    async function fetchUserDataAndBalance() {
      try {
        const currentUser = await getCurrentUser();
        setUser(currentUser);

        if (currentUser?.accountNumber) {
          const initialTransactions = await bankingService.fetchTransactions(
            currentUser.accountNumber
          );
          setTransactions(initialTransactions);

          const userBalance = await bankingService.getBalance(
            currentUser.accountNumber
          );
          setBalance(userBalance);
        }
      } catch (error) {
        console.error("Error fetching user data or transactions:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchUserDataAndBalance();
  }, []); // Removed transactions from dependencies to avoid infinite loop

  const getTimeBasedGreeting = () => {
    const hour = new Date().getHours();
    return hour < 12
      ? "Good morning"
      : hour < 18
      ? "Good afternoon"
      : "Good evening";
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  // Aggregating small categories into "Other"
  const aggregateCategories = (transactions, threshold = 0.05) => {
    const sum = transactions.reduce((acc, curr) => acc + curr.amount, 0);
    const categoryCounts = transactions.reduce((acc, { category, amount }) => {
      acc[category] = (acc[category] || 0) + amount;
      return acc;
    }, {});

    const aggregatedData = Object.entries(categoryCounts).reduce(
      (acc, [category, amount]) => {
        if (amount / sum < threshold) {
          acc["Other"] = (acc["Other"] || 0) + amount;
        } else {
          acc[category] = amount;
        }
        return acc;
      },
      {}
    );

    return aggregatedData;
  };

  const accumulatedData = aggregateCategories(transactions);

  const data = Object.keys(accumulatedData).map((category) => ({
    name: category,
    value: accumulatedData[category],
  }));

  const COLORS = [
    "#0088FE",
    "#00C49F",
    "#FFBB28",
    "#FF8042",
    "#8884d8",
    "#82ca9d",
  ];

  const userGreeting = `${getTimeBasedGreeting()}, ${
    user?.firstName?.trim()
      ? `${user.firstName} ${user.lastName || "User"}`
      : "User"
  }!`;

  const toggleBalanceView = () => setShowBalance(!showBalance);

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="nav-brand">
          <h1>Future Bank</h1>
        </div>
        <ul className="nav-links">
          <li>
            <Link to="/accounts-summary">Accounts Summary</Link>
          </li>
          <li>
            <Link to="/fund-transfer">Fund Transfer</Link>
          </li>
          <li>
            <Link to="/payments-bills">Payments & Bills</Link>
          </li>
          <li>
            <Link to="/profile-management">Profile Management</Link>
          </li>
          <li>
            <Link to="/customer-support">Customer Support</Link>
          </li>
          <li>
            <Link to="/logout">Logout</Link>
          </li>
        </ul>
      </nav>

      <header className="dashboard-header">
        <div className="user-greeting">{userGreeting}</div>
        <button className="view-balance-btn" onClick={toggleBalanceView}>
          {showBalance ? "Hide Balance" : "View Balance"}{" "}
          {/* Corrected the button text for toggling */}
        </button>
        {showBalance && (
          <div className="user-balance">Balance: ${balance.toFixed(2)}</div>
        )}
      </header>

      <div className="main-container">
        <div className="dashboard-content">
          <QuickLinks />
          <PersonalizedOffers />
        </div>
        <div>
          <SpendAnalyzer transactions={transactions} />
        </div>

        <div className="expenditure-chart">
          <div style={{ color: "black", fontSize: "25px" }}>
            <h2>Expenditure Breakdown</h2>
          </div>

          <PieChart width={400} height={600}>
            {" "}
            {/* Adjust dimensions as needed */}
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              outerRadius={150} // Adjust radius as needed
              fill="#8884d8"
              dataKey="value"
              nameKey="name"
              label={({ name, percent }) => {
                if (name.length > 10) {
                  return `${name.slice(0, 10)}... ${(percent * 100).toFixed(
                    0
                  )}%`; // Truncate long names
                } else {
                  return `${name} ${(percent * 100).toFixed(0)}%`;
                }
              }}
              startAngle={0}
              endAngle={360}
            >
              {data.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{ fontSize: "14px" }} // Adjust tooltip font size
              labelStyle={{ color: "#000", fontSize: "14px" }} // Adjust tooltip label font size and color
            />
            <Legend />
          </PieChart>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
