package com.acdiorr.buildpilot.config;

import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.entity.enums.MaterialType;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import com.acdiorr.buildpilot.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds a starter material catalog only when the materials table is empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialSeedRunner implements CommandLineRunner {

    private final MaterialRepository materialRepository;

    @Override
    public void run(String... args) {
        if (materialRepository.count() > 0) {
            log.info("Skipping material seed. Table already contains data.");
            return;
        }

        List<Material> starterMaterials = List.of(
                material("Block hueco", MaterialType.BRICK, UnitMeasure.PIECE, "18.50", "Concrete hollow block", true),
                material("Tabique rojo", MaterialType.BRICK, UnitMeasure.PIECE, "9.20", "Traditional red clay brick", true),
                material("Cemento gris", MaterialType.CEMENT, UnitMeasure.BAG, "235.00", "General purpose Portland cement", true),
                material("Arena", MaterialType.SAND, UnitMeasure.M3, "510.00", "Fine construction sand", true),
                material("Grava", MaterialType.GRAVEL, UnitMeasure.M3, "620.00", "Coarse aggregate for concrete", true),
                material("Varilla 3/8", MaterialType.STEEL, UnitMeasure.PIECE, "145.00", "Steel rebar 3/8 in", true),
                material("Varilla 1/2", MaterialType.STEEL, UnitMeasure.PIECE, "198.00", "Steel rebar 1/2 in", true),
                material("Yeso", MaterialType.OTHER, UnitMeasure.BAG, "189.00", "Interior plaster finish", true),
                material("Pintura blanca", MaterialType.PAINT, UnitMeasure.LITER, "85.00", "White acrylic paint", true),
                material("Piso cerámico", MaterialType.FLOORING, UnitMeasure.M2, "315.00", "Ceramic floor tile", true),
                material("Puerta madera", MaterialType.WOOD, UnitMeasure.PIECE, "2450.00", "Standard wooden door", true),
                material("Ventana aluminio", MaterialType.GLASS, UnitMeasure.PIECE, "1980.00", "Aluminum sliding window", true)
        );

        materialRepository.saveAll(starterMaterials);
        log.info("Seeded {} starter materials.", starterMaterials.size());
    }

    private Material material(
            String name,
            MaterialType materialType,
            UnitMeasure unitMeasure,
            String unitPrice,
            String description,
            boolean isActive
    ) {
        return Material.builder()
                .name(name)
                .materialType(materialType)
                .unitMeasure(unitMeasure)
                .unitPrice(new BigDecimal(unitPrice))
                .description(description)
                .isActive(isActive)
                .build();
    }
}

