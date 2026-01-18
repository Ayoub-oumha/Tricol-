package org.tricol.supplierchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class Brief6TricolSupplierChainApplication {
    public static void main(String[] args) {
        SpringApplication.run(Brief6TricolSupplierChainApplication.class, args);
    }

}
