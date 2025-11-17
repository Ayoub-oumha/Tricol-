package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;
import org.tricol.supplierchain.entity.Fournisseur;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T09:13:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FournisseurMapperImpl implements FournisseurMapper {

    @Override
    public Fournisseur toEntity(FournisseurRequestDTO fournisseurRequestDTO) {
        if ( fournisseurRequestDTO == null ) {
            return null;
        }

        Fournisseur fournisseur = new Fournisseur();

        fournisseur.setAdresse( fournisseurRequestDTO.getAdresse() );
        fournisseur.setEmail( fournisseurRequestDTO.getEmail() );
        fournisseur.setIce( fournisseurRequestDTO.getIce() );
        fournisseur.setPersonneContact( fournisseurRequestDTO.getPersonneContact() );
        fournisseur.setRaisonSociale( fournisseurRequestDTO.getRaisonSociale() );
        fournisseur.setTelephone( fournisseurRequestDTO.getTelephone() );
        fournisseur.setVille( fournisseurRequestDTO.getVille() );

        return fournisseur;
    }

    @Override
    public FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur) {
        if ( fournisseur == null ) {
            return null;
        }

        FournisseurResponseDTO fournisseurResponseDTO = new FournisseurResponseDTO();

        fournisseurResponseDTO.setAdresse( fournisseur.getAdresse() );
        fournisseurResponseDTO.setDateCreation( fournisseur.getDateCreation() );
        fournisseurResponseDTO.setDateModification( fournisseur.getDateModification() );
        fournisseurResponseDTO.setEmail( fournisseur.getEmail() );
        fournisseurResponseDTO.setIce( fournisseur.getIce() );
        fournisseurResponseDTO.setId( fournisseur.getId() );
        fournisseurResponseDTO.setPersonneContact( fournisseur.getPersonneContact() );
        fournisseurResponseDTO.setRaisonSociale( fournisseur.getRaisonSociale() );
        fournisseurResponseDTO.setTelephone( fournisseur.getTelephone() );
        fournisseurResponseDTO.setVille( fournisseur.getVille() );

        return fournisseurResponseDTO;
    }

    @Override
    public void updateEntityFromDto(FournisseurUpdateDTO dto, Fournisseur fournisseur) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getAdresse() != null ) {
            fournisseur.setAdresse( dto.getAdresse() );
        }
        if ( dto.getEmail() != null ) {
            fournisseur.setEmail( dto.getEmail() );
        }
        if ( dto.getIce() != null ) {
            fournisseur.setIce( dto.getIce() );
        }
        if ( dto.getPersonneContact() != null ) {
            fournisseur.setPersonneContact( dto.getPersonneContact() );
        }
        if ( dto.getRaisonSociale() != null ) {
            fournisseur.setRaisonSociale( dto.getRaisonSociale() );
        }
        if ( dto.getTelephone() != null ) {
            fournisseur.setTelephone( dto.getTelephone() );
        }
        if ( dto.getVille() != null ) {
            fournisseur.setVille( dto.getVille() );
        }
    }
}
