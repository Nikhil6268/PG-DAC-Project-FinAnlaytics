import React, { useEffect, useState } from "react";
import BankingService from "../../Services/bankingService"; // Ensure this path is correct
import "./AccountsSummary.css";
import { useUser } from "../../context/UserContext";

function AccountsSummary() {
  const [transactions, setTransactions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user } = useUser(); // Assuming useUser() hook provides user and loading state

  useEffect(() => {
    // Clear transactions when user changes to prevent displaying previous user's data
    setTransactions([]);
    setIsLoading(true);
    setError(null);

    if (!user || !user.accountNumber) {
      console.error("No user or account ID available");
      setIsLoading(false); // Ensure loading is set to false if there's no user
      return;
    }

    const fetchAccountTransactions = async () => {
      try {
        const fetchedTransactions = await BankingService.fetchTransactions(
          user.accountNumber
        );
        setTransactions(fetchedTransactions);
      } catch (error) {
        console.error("Failed to fetch transactions:", error);
        setError(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAccountTransactions();

    // Optionally, if your component might unmount before the fetch completes:
    // return a cleanup function to cancel the fetch or ignore its result.
    // This depends on how `BankingService.fetchTransactions` is implemented.
    // For example, if using Axios, you might use an Axios cancel token here.
  }, [user]); // Reacts to changes in user object, specifically if user changes

  if (isLoading) {
    return <div>Loading account transactions...</div>;
  }

  if (error) {
    return (
      <div>Error fetching account transactions. Please try again later.</div>
    );
  }

  // Rendering component content
  return (
    <div class="transaction-summary">
      <h2>Account Transactions</h2>
      {transactions.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>Transaction ID</th>
              <th>From Account</th>
              <th>To Account</th>
              <th>Amount</th>
              <th>Category</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <tr key={transaction.transactionId}>
                <td>{transaction.transactionId}</td>
                <td>{transaction.fromAccount.accountNumber}</td>
                <td>{transaction.toAccount.accountNumber}</td>
                <td>${transaction.amount}</td>
                <td>{transaction.category}</td>
                <td>{transaction.transactionDate}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No transactions found.</p>
      )}
    </div>
  );
}

export default AccountsSummary;
