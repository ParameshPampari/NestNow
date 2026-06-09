package com.nestnow.config;

import com.nestnow.entity.Category;
import com.nestnow.entity.ServiceEntity;
import com.nestnow.repository.CategoryRepository;
import com.nestnow.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) {

        if (serviceRepository.count() > 0) {
            return;
        }

        seedCategory(
                "Plumbing",
                "Repairs and installations for taps, pipes, leaks, and bathroom fixtures",
                List.of(
                        service("Tap Repair", "Fix dripping or loose taps", 299.0, 45),
                        service("Leak Detection", "Inspect and repair visible water leakage", 499.0, 60),
                        service("Pipe Replacement", "Replace damaged exposed pipe sections", 899.0, 90),
                        service("Toilet Repair", "Repair flush tanks, seat fittings, and blockages", 599.0, 75),
                        service("Sink Installation", "Install kitchen or bathroom sink fittings", 999.0, 120),
                        service("Drain Unclogging", "Clear slow or blocked drains", 499.0, 60),
                        service("Geyser Pipe Fitting", "Install inlet and outlet geyser pipe fittings", 699.0, 75),
                        service("Bathroom Fitting", "Install faucets, showers, and accessories", 799.0, 90),
                        service("Water Tank Cleaning", "Clean domestic overhead water tanks", 1299.0, 180),
                        service("Water Motor Repair", "Basic water pump inspection and repair", 899.0, 90)
                )
        );

        seedCategory(
                "Electrical",
                "Electrical repairs, fixture installation, and safety checks",
                List.of(
                        service("Switchboard Repair", "Repair switches, sockets, and plates", 299.0, 45),
                        service("Fan Installation", "Install ceiling or wall-mounted fans", 499.0, 60),
                        service("Light Installation", "Install tube lights, panels, or fixtures", 399.0, 45),
                        service("MCB Repair", "Inspect and replace faulty MCB units", 599.0, 60),
                        service("Inverter Setup", "Connect inverter and battery wiring", 999.0, 120),
                        service("Doorbell Repair", "Repair wired or wireless doorbell units", 299.0, 45),
                        service("Wiring Inspection", "Inspect exposed wiring and safety risks", 699.0, 90),
                        service("Power Socket Installation", "Install new electrical socket point", 599.0, 75),
                        service("Chandelier Installation", "Install decorative ceiling lighting", 1199.0, 120),
                        service("Appliance Connection", "Connect high-load appliances safely", 499.0, 60)
                )
        );

        seedCategory(
                "AC & Appliances",
                "AC, refrigerator, washing machine, and appliance support",
                List.of(
                        service("AC Service", "Wet service for split or window AC", 699.0, 90),
                        service("AC Gas Refill", "Inspect leakage and refill AC gas", 2499.0, 120),
                        service("AC Installation", "Install split AC indoor and outdoor units", 1999.0, 180),
                        service("Refrigerator Checkup", "Diagnose cooling and compressor issues", 499.0, 60),
                        service("Washing Machine Repair", "Inspect drainage, spin, and motor faults", 599.0, 75),
                        service("Microwave Repair", "Basic diagnosis and repair estimate", 499.0, 60),
                        service("Water Purifier Service", "Filter check and purifier servicing", 599.0, 75),
                        service("TV Wall Mounting", "Mount LED TV on wall bracket", 799.0, 90),
                        service("Geyser Service", "Inspect heating, wiring, and safety valve", 599.0, 75),
                        service("Chimney Cleaning", "Deep clean kitchen chimney filters", 999.0, 120)
                )
        );

        seedCategory(
                "Carpentry",
                "Furniture repair, fitting, assembly, and home woodwork",
                List.of(
                        service("Door Hinge Repair", "Fix loose or damaged door hinges", 399.0, 45),
                        service("Furniture Assembly", "Assemble flat-pack home furniture", 799.0, 90),
                        service("Curtain Rod Installation", "Install curtain rods and brackets", 399.0, 45),
                        service("Shelf Installation", "Mount wall shelves safely", 499.0, 60),
                        service("Bed Repair", "Repair loose bed frame or storage fittings", 699.0, 75),
                        service("Wardrobe Repair", "Repair hinges, handles, and sliding channels", 699.0, 75),
                        service("Door Lock Installation", "Install or replace door locks", 599.0, 60),
                        service("Kitchen Cabinet Repair", "Repair cabinet hinges and shutters", 799.0, 90),
                        service("Wooden Partition Repair", "Repair minor wooden partition damage", 999.0, 120),
                        service("Drill and Hang", "Drill and hang frames, mirrors, or decor", 299.0, 30)
                )
        );

        seedCategory(
                "Cleaning & Painting",
                "Deep cleaning, pest preparation, painting, and surface care",
                List.of(
                        service("Bathroom Cleaning", "Deep clean one bathroom", 599.0, 90),
                        service("Kitchen Cleaning", "Degrease and deep clean kitchen area", 999.0, 150),
                        service("Sofa Cleaning", "Shampoo clean fabric sofa", 899.0, 120),
                        service("Full Home Cleaning", "Deep clean 1BHK home", 2499.0, 300),
                        service("Move-in Cleaning", "Clean empty home before move-in", 2999.0, 360),
                        service("Wall Painting", "Paint one wall with standard finish", 1499.0, 240),
                        service("Touch-up Painting", "Minor paint touch-up and patchwork", 799.0, 120),
                        service("Waterproofing Check", "Inspect seepage and recommend repair", 499.0, 60),
                        service("Balcony Cleaning", "Deep clean balcony floor and railings", 499.0, 75),
                        service("Mattress Cleaning", "Vacuum and shampoo clean mattress", 799.0, 90)
                )
        );
    }

    private void seedCategory(
            String name,
            String description,
            List<ServiceEntity> services
    ) {

        Category category = categoryRepository.existsByName(name)
                ? categoryRepository.findAll()
                .stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElseThrow()
                : categoryRepository.save(Category.builder()
                .name(name)
                .description(description)
                .build());

        services.forEach(service -> {
            service.setCategory(category);
            serviceRepository.save(service);
        });
    }

    private ServiceEntity service(
            String title,
            String description,
            Double price,
            Integer duration
    ) {

        return ServiceEntity.builder()
                .title(title)
                .description(description)
                .price(price)
                .duration(duration)
                .active(true)
                .build();
    }
}
