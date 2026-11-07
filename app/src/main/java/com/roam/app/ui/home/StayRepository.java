package com.roam.app.ui.home;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class StayRepository {
    private static final List<Stay> STAYS = Collections.unmodifiableList(Arrays.asList(
            new Stay(
                    "lake-arrowhead-cabin",
                    "Modern Cabin with Hot Tub",
                    "Lake Arrowhead, CA",
                    "Cabins",
                    "A quiet, pet friendly pine-forest cabin with a private hot tub, fire pit, and room for your dog.",
                    "https://images.unsplash.com/photo-1636886519725-6a048800b5b4?w=1200&q=85&auto=format&fit=crop",
                    4.96,
                    128,
                    248
            ),
            new Stay(
                    "malibu-coast-house",
                    "Coastal Light House",
                    "Malibu, CA",
                    "Beach",
                    "Wake up to ocean air in a bright, design-forward home a short walk from the sand.",
                    "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=1200&q=85&auto=format&fit=crop",
                    4.91,
                    86,
                    385
            ),
            new Stay(
                    "hudson-loft",
                    "Sunlit Loft near the Arts District",
                    "Brooklyn, NY",
                    "City",
                    "A calm, plant-filled loft close to neighborhood cafes, galleries, and the subway.",
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&q=85&auto=format&fit=crop",
                    4.88,
                    203,
                    219
            ),
            new Stay(
                    "ojai-oak-villa",
                    "Oak Grove Pool Villa",
                    "Ojai, CA",
                    "Villas",
                    "A private hillside retreat with a pool, outdoor dining, and mountain sunset views.",
                    "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=1200&q=85&auto=format&fit=crop",
                    4.98,
                    64,
                    520
            ),
            new Stay(
                    "bend-river-cabin",
                    "Riverbend A-Frame",
                    "Bend, OR",
                    "Cabins",
                    "A cedar A-frame with a wood stove, river trail access, and a fenced yard for pets.",
                    "https://images.unsplash.com/photo-1449158743715-0a90ebb6d2d8?w=1200&q=85&auto=format&fit=crop",
                    4.93,
                    97,
                    195
            ),
            new Stay(
                    "kauai-beach-bungalow",
                    "Garden Beach Bungalow",
                    "Kauaʻi, HI",
                    "Beach",
                    "A relaxed island bungalow tucked into tropical gardens near a quiet swimming beach.",
                    "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=1200&q=85&auto=format&fit=crop",
                    4.89,
                    111,
                    310
            )
    ));

    private StayRepository() {
    }

    public static List<Stay> getStays() {
        return STAYS;
    }
}
