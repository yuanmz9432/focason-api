package com.lemonico.api;



import java.math.BigDecimal;

/**
 * 計算ツール
 */
public class Calculator
{

    public static int priceIncludeTax(int price, int tax) {
        BigDecimal bigPrice = new BigDecimal(price);
        BigDecimal BigTax = new BigDecimal(tax).divide(BigDecimal.valueOf(100));
        BigDecimal bigTaxRate = BigDecimal.valueOf(1).add(BigTax);
        return bigPrice.multiply(bigTaxRate).intValue();
    }

    public static int sum(int priceIncludeTax, int num) {
        BigDecimal bigPrice = new BigDecimal(priceIncludeTax);
        BigDecimal bigNum = new BigDecimal(num);
        return bigPrice.multiply(bigNum).intValue();
    }
}
