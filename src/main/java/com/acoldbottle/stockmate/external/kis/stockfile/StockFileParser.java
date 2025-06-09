package com.acoldbottle.stockmate.external.kis.stockfile;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockFileParser {

    @Value("${kis.download.dir}")
    private String downloadDir;

    public List<StockDTO> parseAll() {

        Path nasdaqPath = Paths.get(downloadDir, "NASMST.COD");
        Path nysePath = Paths.get(downloadDir, "NYSMST.COD");
        Path amexPath = Paths.get(downloadDir, "AMSMST.COD");
        List<StockDTO> result = new ArrayList<>();
        try {
            List<StockDTO> nasdaqList = parse(nasdaqPath, "nasdaq");
            List<StockDTO> nyseList = parse(nysePath, "nyse");
            List<StockDTO> amexList = parse(amexPath, "amex");

            result.addAll(nasdaqList);
            result.addAll(nyseList);
            result.addAll(amexList);
        } catch (IOException e) {
            throw new KisFileParseException(ErrorCode.KIS_FILE_PARSE_ERROR);
        }

        return result;
    }

    private List<StockDTO> parse(Path filePath, String marketCode) throws IOException {

        List<StockDTO> stockList = new ArrayList<>();

        BufferedReader reader = Files.newBufferedReader(filePath, Charset.forName("EUC-KR"));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] fields = line.split("\t", -1);

            StockDTO stockDTO = StockDTO.builder()
                    .symbol(fields[4])
                    .korName(containsHangul(fields[6]) ? fields[6] : "")
                    .engName(fields[7])
                    .marketCode(marketCode)
                    .build();

            stockList.add(stockDTO);
        }

        return stockList;
    }

    private boolean containsHangul(String s) {
        if (s == null) return false;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                return true;
            }
        }
        return false;
    }
}
