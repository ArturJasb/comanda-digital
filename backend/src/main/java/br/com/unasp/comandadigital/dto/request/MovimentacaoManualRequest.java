package br.com.unasp.comandadigital.dto.request;

import br.com.unasp.comandadigital.model.MotivoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovimentacaoManualRequest {

    @NotNull
    private Long ingredienteId;

    @NotNull
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    // RF-30: saida manual exige motivo (DESPERDICIO, VENCIMENTO, USO_INTERNO, AJUSTE)
    @NotNull
    private MotivoMovimentacao motivo;

    private String lote;

    private LocalDate validade;
}
