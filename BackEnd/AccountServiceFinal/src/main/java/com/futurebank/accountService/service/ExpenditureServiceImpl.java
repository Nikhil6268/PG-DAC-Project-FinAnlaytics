package com.futurebank.accountService.service;

import com.futurebank.accountService.DTO.ForecastDTO;
import com.futurebank.accountService.DTO.MonthlyExpenditureDTO;
import com.futurebank.accountService.model.MyTransactionCategory;
import com.futurebank.accountService.model.Transaction;
import com.futurebank.accountService.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenditureServiceImpl implements ExpenditureService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenditureServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<MonthlyExpenditureDTO> getMonthlyExpenditures() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYear = now.withDayOfYear(1).toLocalDate().atStartOfDay();
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startOfYear, now);

        Map<YearMonth, Map<MyTransactionCategory, BigDecimal>> monthlyExpenditures = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> YearMonth.from(transaction.getTransactionDate()),
                        Collectors.groupingBy(Transaction::getCategory,
                                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add))));

        List<MonthlyExpenditureDTO> results = new ArrayList<>();
        monthlyExpenditures.forEach((yearMonth, categoryMap) -> categoryMap.forEach((category, total) -> {
            results.add(new MonthlyExpenditureDTO(category, total, yearMonth));
        }));

        logger.info("Returning monthly expenditures: {}", results);
        return results;
    }

    @Override
    public ForecastDTO getExpenditureForecast() {
        try {
            BigDecimal forecastValue = getForecastValueBasedOnRecentMonths();
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("data", forecastValue);

            String forecastApiUrl = "http://127.0.0.1:5000/forecast";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<ForecastDTO> response = restTemplate.postForEntity(forecastApiUrl, entity, ForecastDTO.class);
            logger.info("Forecast result: {}", response.getBody());
            return response.getBody();
        } catch (Exception ex) {
            logger.error("Error during forecasting: {}", ex.getMessage());
            return new ForecastDTO(); // Consider enriching this placeholder for actual error handling
        }
    }

    private BigDecimal getForecastValueBasedOnRecentMonths() {
        LocalDate now = LocalDate.now();
        LocalDate fourMonthsAgo = now.minusMonths(3).withDayOfMonth(1); // Adjust to include the last 4 months

        List<MonthlyExpenditureDTO> recentExpenditures = getMonthlyExpendituresBetween(fourMonthsAgo, now);

        if (!recentExpenditures.isEmpty()) {
            return recentExpenditures.stream()
                    .map(MonthlyExpenditureDTO::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(recentExpenditures.size()), BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal("1000"); // Example default fallback value
        }
    }

    private List<MonthlyExpenditureDTO> getMonthlyExpendituresBetween(LocalDate start, LocalDate end) {
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        List<MonthlyExpenditureDTO> expenditures = new ArrayList<>();

        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            expenditures.addAll(getMonthlyExpendituresForMonth(month));
        }

        return expenditures;
    }

    private List<MonthlyExpenditureDTO> getMonthlyExpendituresForMonth(YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(start, end);

        Map<MyTransactionCategory, BigDecimal> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        List<MonthlyExpenditureDTO> results = new ArrayList<>();
        categoryTotals.forEach((category, total) -> results.add(new MonthlyExpenditureDTO(category, total, month)));
        logger.info("Monthly expenditures for month {}: {}", month, results);
        return results;
    }
}
