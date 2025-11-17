package org.tricol.supplierchain.dto.response;

import java.util.List;

public class StockInsuffisantResponseDTO {
    private String message;
    private String numeroBonSortie;
    private List<DeficitStockResponseDTO> deficits;
    private List<String> actionsPossibles;
}
