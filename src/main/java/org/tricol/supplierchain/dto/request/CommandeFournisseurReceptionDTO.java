package org.tricol.supplierchain.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurReceptionDTO {

    @NotNull(message = "La date de livraison effective est obligatoire")
    private LocalDate dateLivraisonEffective;
}
