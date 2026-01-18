package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.MouvementStockSearchCriteria;
import org.tricol.supplierchain.dto.response.MouvementStockResponseDTO;
import org.tricol.supplierchain.entity.MouvementStock;
import org.tricol.supplierchain.mapper.MouvementStockMapper;
import org.tricol.supplierchain.repository.MouvementStockRepository;
import org.tricol.supplierchain.specification.MouvementStockSpecifications;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MouvementStockSearchService {
    
    private final MouvementStockRepository mouvementStockRepository;
    private final MouvementStockMapper mapper;
    
    public Page<MouvementStockResponseDTO> searchMouvements(
            MouvementStockSearchCriteria criteria, 
            Pageable pageable) {
        
        Specification<MouvementStock> spec = MouvementStockSpecifications.withCriteria(criteria);
        Page<MouvementStock> mouvements = mouvementStockRepository.findAll(spec, pageable);
        return mouvements.map(mapper::toResponseDTO);
    }
    

}