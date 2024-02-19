import React, { useState, useEffect } from 'react';
import { fetchMonthlyExpenditures, fetchExpenditureForecast } from '../../Services/expenditureService';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import "./SpendAnalyzer.css";

function SpendAnalyzer() {
  const [monthlyExpenditures, setMonthlyExpenditures] = useState([]);
  const [forecast, setForecast] = useState([]);

  useEffect(() => {
    const loadExpendituresAndForecast = async () => {
      try {
        const monthlyData = await fetchMonthlyExpenditures();
        setMonthlyExpenditures(monthlyData);
        const forecastData = await fetchExpenditureForecast();
        // Ensure forecast data is treated as an array
        setForecast(Array.isArray(forecastData) ? forecastData : [forecastData]);
      } catch (error) {
        console.error('Error fetching expenditure data:', error);
      }
    };

    loadExpendituresAndForecast();
  }, []);

  // Mapping month numbers to month names
  const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

  const getMonthName = (monthNumber) => {
    // Assuming month is provided as a 1-based number (1 for January, 12 for December)
    return monthNames[monthNumber - 1] || monthNumber;
  };

  // Function to transform data for chart
  const transformDataForChart = (expenditures, forecast) => {
    let dataByMonth = {};
    expenditures.forEach(({ month, category, totalAmount }) => {
      const monthName = getMonthName(month);
      if (!dataByMonth[monthName]) {
        dataByMonth[monthName] = {};
      }
      dataByMonth[monthName][category] = totalAmount;
    });

    forecast.forEach(({ month, category, forecastAmount }) => {
      const monthName = getMonthName(month);
      if (!dataByMonth[monthName]) {
        dataByMonth[monthName] = {};
      }
      dataByMonth[monthName][category] = forecastAmount;
    });

    return Object.keys(dataByMonth).map(month => ({
      month,
      ...dataByMonth[month],
    }));
  };

  const chartData = transformDataForChart(monthlyExpenditures, forecast);

  const renderCharts = () => {
    return chartData.map((data, index) => (
      <div class="SpendAnalyzer" key={index}>
        <h3>{data.month}</h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={[data]}>
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip />
            <Legend />
            {Object.keys(data).filter(key => key !== 'month').map((key, idx) => (
              <Bar key={idx} dataKey={key} fill={`#${Math.floor(Math.random()*16777215).toString(16)}`} />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </div>
    ));
  };

  return (
    <div class ="SpendAnalyzer-label">
      <h2>Monthly Expenditures and Forecast</h2>
      {renderCharts()}
    </div>
  );
}

export default SpendAnalyzer;
