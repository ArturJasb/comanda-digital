package br.com.unasp.comandadigital.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formulas de custo e food cost (SRS 3.2 / RF-12 / RF-13).
 * custo_item     = qtd x fator_correcao x custo_unitario
 * custo_total    = SUM(custo_item) / rendimento
 * food_cost_pct  = (custo_total / preco_venda) x 100
 */
public final class FoodCostUtil {

    public static final BigDecimal LIMITE_VERDE = new BigDecimal("30");
    public static final BigDecimal LIMITE_AMARELO = new BigDecimal("35");

    private FoodCostUtil() {}

    /** Custo de um item da ficha: qtd x fator_correcao x custo_unitario. */
    public static BigDecimal custoItem(BigDecimal quantidade, BigDecimal fatorCorrecao, BigDecimal custoUnitario) {
        return nz(quantidade).multiply(nz(fatorCorrecao)).multiply(nz(custoUnitario));
    }

    /** Custo total da ficha = soma dos itens dividida pelo rendimento. */
    public static BigDecimal custoTotal(BigDecimal somaItens, int rendimento) {
        int r = rendimento <= 0 ? 1 : rendimento;
        return nz(somaItens).divide(BigDecimal.valueOf(r), 2, RoundingMode.HALF_UP);
    }

    /** food_cost_pct = custo / preco x 100. Retorna 0 se preco invalido. */
    public static BigDecimal foodCostPct(BigDecimal custoTotal, BigDecimal precoVenda) {
        if (precoVenda == null || precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return nz(custoTotal)
                .divide(precoVenda, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Cor do food cost: verde (<=30%), amarelo (31-35%), vermelho (>35%). */
    public static String cor(BigDecimal pct) {
        if (pct == null) return "verde";
        if (pct.compareTo(LIMITE_VERDE) <= 0) return "verde";
        if (pct.compareTo(LIMITE_AMARELO) <= 0) return "amarelo";
        return "vermelho";
    }

    /** RN-02: aviso quando food cost passa de 35% (nao bloqueia). */
    public static String warning(BigDecimal pct) {
        if (pct != null && pct.compareTo(LIMITE_AMARELO) > 0) {
            return "Food cost acima de 35% (" + pct.stripTrailingZeros().toPlainString() + "%). Revise a margem.";
        }
        return null;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
