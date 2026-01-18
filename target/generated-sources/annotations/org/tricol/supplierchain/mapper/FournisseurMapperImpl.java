package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.FournisseurRequestDTO;
import org.tricol.supplierchain.dto.request.FournisseurUpdateDTO;
import org.tricol.supplierchain.dto.response.FournisseurResponseDTO;
import org.tricol.supplierchain.entity.Fournisseur;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class FournisseurMapperImpl implements FournisseurMapper {

    @Override
    public Fournisseur toEntity(FournisseurRequestDTO fournisseurRequestDTO) {
        if ( fournisseurRequestDTO == null ) {
            return null;
        }

        Fournisseur fournisseur = new Fournisseur();

        fournisseur.setRaisonSociale( fournisseurRequestDTO.getRaisonSociale() );
        fournisseur.setAdresse( fournisseurRequestDTO.getAdresse() );
        fournisseur.setVille( fournisseurRequestDTO.getVille() );
        fournisseur.setPersonneContact( fournisseurRequestDTO.getPersonneContact() );
        fournisseur.setEmail( fournisseurRequestDTO.getEmail() );
        fournisseur.setTelephone( fournisseurRequestDTO.getTelephone() );
        fournisseur.setIce( fournisseurRequestDTO.getIce() );

        return fournisseur;
    }

    @Override
    public FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur) {
        if ( fournisseur == null ) {
            return null;
        }

        FournisseurResponseDTO fournisseurResponseDTO = new FournisseurResponseDTO();

        fournisseurResponseDTO.setId( fournisseur.getId() );
        fournisseurResponseDTO.setRaisonSociale( fournisseur.getRaisonSociale() );
        fournisseurResponseDTO.setAdresse( fournisseur.getAdresse() );
        fournisseurResponseDTO.setVille( fournisseur.getVille() );
        fournisseurResponseDTO.setPersonneContact( fournisseur.getPersonneContact() );
        fournisseurResponseDTO.setEmail( fournisseur.getEmail() );
        fournisseurResponseDTO.setTelephone( fournisseur.getTelephone() );
        fournisseurResponseDTO.setIce( fournisseur.getIce() );
        fournisseurResponseDTO.setDateCreation( fournisseur.getDateCreation() );
        fournisseurResponseDTO.setDateModification( fournisseur.getDateModification() );

        return fournisseurResponseDTO;
    }

    @Override
    public void updateEntityFromDto(FournisseurUpdateDTO dto, Fournisseur fournisseur) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getRaisonSociale() != null ) {
            fournisseur.setRaisonSociale( dto.getRaisonSociale() );
        }
        if ( dto.getAdresse() != null ) {
            fournisseur.setAdresse( dto.getAdresse() );
        }
        if ( dto.getVille() != null ) {
            fournisseur.setVille( dto.getVille() );
        }
        if ( dto.getPersonneContact() != null ) {
            fournisseur.setPersonneContact( dto.getPersonneContact() );
        }
        if ( dto.getEmail() != null ) {
            fournisseur.setEmail( dto.getEmail() );
        }
        if ( dto.getTelephone() != null ) {
            fournisseur.setTelephone( dto.getTelephone() );
        }
        if ( dto.getIce() != null ) {
            fournisseur.setIce( dto.getIce() );
        }
    }
}
