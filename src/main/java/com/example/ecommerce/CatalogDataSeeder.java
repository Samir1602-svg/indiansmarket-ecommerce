package com.example.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class CatalogDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public CatalogDataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() >= 200) {
            return;
        }

        List<Product> catalog = new ArrayList<>();
        Random rand = new Random();

        String[][] baseCatalog = {
            // MOBILES & TECH
            {"Apple iPhone 15 Pro Max (256 GB) - Blue Titanium", "Electronics", "Apple", "148900", "159900", "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=700", "Appario Retail Pvt Ltd", "DIAMOND", "Delhi NCR Hub", "Titanium design, A17 Pro chip, 48MP main camera, USB-C."},
            {"Samsung Galaxy S24 Ultra 5G AI (12GB RAM, 512GB)", "Electronics", "Samsung", "129999", "144999", "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=700", "Infiniti Retail (Croma)", "DIAMOND", "Mumbai Hub", "Galaxy AI, 200MP camera, Snapdragon 8 Gen 3, S-Pen included."},
            {"Sony WH-1000XM5 Wireless Active Noise Cancelling", "Electronics", "Sony", "29990", "34990", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=700", "Appario Retail Pvt Ltd", "GOLD", "Bengaluru Hub", "Industry leading ANC with 2 processors, 30 hours battery."},
            {"Apple MacBook Air 15-inch M3 Chip (16GB RAM, 512GB)", "Electronics", "Apple", "134900", "144900", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=700", "Appario Retail Pvt Ltd", "DIAMOND", "Delhi NCR Hub", "Liquid Retina display, MagSafe 3 charging, 18-hour battery life."},
            {"Logitech MX Master 3S Ergonomic Wireless Mouse", "Electronics", "Logitech", "8995", "10995", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=700", "Cloudtail Tech", "SILVER", "Hyderabad Hub", "8K DPI track-on-glass sensor, quiet clicks, electromagnetic scrolling."},
            {"Samsung 55-inch Crystal 4K UHD Smart TV", "Electronics", "Samsung", "42990", "64900", "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=700", "Appario Retail Pvt Ltd", "DIAMOND", "Delhi NCR Hub", "Dynamic Crystal Color, HDR10+, OTS Lite object tracking sound."},
            {"OnePlus 12 5G (Flowy Emerald 16GB, 512GB)", "Electronics", "OnePlus", "64999", "69999", "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=700", "Cloudtail Tech", "GOLD", "Bengaluru Hub", "4th Gen Hasselblad Camera, Snapdragon 8 Gen 3, 5400mAh battery."},
            {"Sony PlayStation 5 Console (Slim Disc Edition)", "Electronics", "Sony", "54990", "59990", "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=700", "Appario Retail Pvt Ltd", "DIAMOND", "Mumbai Hub", "Ultra-high speed SSD, haptic feedback, adaptive triggers and 3D Audio."},

            // FASHION & APPAREL
            {"Men's Slim Fit Washed Denim Trucker Jacket", "Fashion", "Levi's", "3199", "5499", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=700", "Cloudtail Fashion", "GOLD", "Delhi NCR Hub", "100% breathable organic cotton, dual flap chest pockets."},
            {"Women's Pure Kanchipuram Soft Silk Zari Saree", "Fashion", "Kalyan Silks", "4499", "8999", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=700", "Appario Fashion", "DIAMOND", "Chennai Hub", "Authentic gold zari pallu with unstitched matching blouse piece."},
            {"Casual Lightweight Breathable Running Sneakers", "Fashion", "Nike", "3795", "5495", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=700", "Nike Authorized Store", "GOLD", "Bengaluru Hub", "Air Zoom cushioning, high-traction waffle rubber outsole."},
            {"Chronograph Black Stainless Steel Dial Analog Watch", "Fashion", "Fossil", "8995", "14995", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=700", "Titan & Fossil Official", "GOLD", "Delhi NCR Hub", "44mm case, quartz chronograph movement, scratch-resistant mineral crystal."},
            {"Genuine Handcrafted Leather Weekender Duffel Bag", "Fashion", "Wildhorn", "3499", "7999", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=700", "LeatherCraft India", "SILVER", "Kolkata Hub", "Full grain buffalo leather with brass hardware, detachable shoulder strap."},

            // HOME & KITCHEN
            {"Prestige IRIS 750 Watt Mixer Grinder with 3 Jars", "HomeKitchen", "Prestige", "3199", "6295", "https://images.unsplash.com/photo-1588854337236-6889d631faa8?w=700", "Appario Retail Pvt Ltd", "DIAMOND", "Bengaluru Hub", "Heavy duty 750W copper motor with overload protection, 3 jars."},
            {"Philips Digital Air Fryer with Rapid Air Tech 4.1L", "HomeKitchen", "Philips", "7499", "12995", "https://images.unsplash.com/photo-1585515320310-259814833e62?w=700", "Cloudtail Kitchen", "GOLD", "Delhi NCR Hub", "Fry with up to 90% less fat, Keep Warm function, Touchscreen."},
            {"Kent Grand Plus RO+UV+UF Water Purifier 9L Tank", "HomeKitchen", "Kent", "14499", "21000", "https://images.unsplash.com/photo-1548839140-29a749e1bc4e?w=700", "Kent Direct", "DIAMOND", "Noida Hub", "In-tank UV disinfection, TDS control valve, Zero Water Wastage pump."},
            {"Solid Sheesham Wood 6-Seater Cushioned Dining Set", "HomeKitchen", "Wakefit", "22999", "38999", "https://images.unsplash.com/photo-1617806118233-18e1de247200?w=700", "Wakefit Furnishings", "GOLD", "Bengaluru Hub", "Natural teak finish solid wood frame, premium cushioned chairs."},
            {"Hawkins Contura Hard Anodized 5L Pressure Cooker", "HomeKitchen", "Hawkins", "2250", "2850", "https://images.unsplash.com/photo-1584990347449-397a61d15a51?w=700", "Hawkins Direct", "GOLD", "Mumbai Hub", "Hard anodized body with stainless steel lid, pressure locked safety."},

            // FITNESS & SPORTS
            {"Adjustable Hexagonal Rubber Encased Dumbbell Pair", "Fitness", "Kore", "2499", "4999", "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=700", "FitIndia Sports", "SILVER", "Meerut Hub", "Anti-roll hexagonal rubber casing with knurled chrome grip handle."},
            {"Yonex Muscle Power 29 Lite Carbon High Tension Racket", "Fitness", "Yonex", "2290", "3490", "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=700", "Sunrise Sports India", "DIAMOND", "Delhi NCR Hub", "High modulus graphite isometric frame, 30 lbs string tension."},
            {"Spalding NBA Composite Leather Match Basketball", "Fitness", "Spalding", "1899", "2999", "https://images.unsplash.com/photo-1519861531473-9200262188bf?w=700", "Cloudtail Sports", "GOLD", "Mumbai Hub", "Deep channel design for superior grip control, suitable for hard courts."},

            // GROCERY & PANTRY
            {"Borges Cold-Pressed Extra Virgin Olive Oil 1L", "Grocery", "Borges", "899", "1450", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=700", "Appario Fresh", "DIAMOND", "Delhi NCR Hub", "First cold-extracted from Mediterranean olives, rich in natural antioxidants."},
            {"Daawat Rozana Super Basmati Rice (5kg Bag)", "Grocery", "Daawat", "475", "650", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=700", "Appario Fresh", "DIAMOND", "Delhi NCR Hub", "Aged long grain aromatic basmati rice, pristine pearl white texture."},
            {"Happilo 100% Natural California Raw Almonds 1kg", "Grocery", "Happilo", "799", "1299", "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=700", "Appario Fresh", "DIAMOND", "Bengaluru Hub", "High in dietary fiber, Vitamin E and plant protein."},

            // BOOKS
            {"Atomic Habits: Tiny Changes, Remarkable Results", "Books", "James Clear", "499", "799", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=700", "Cloudtail Books", "DIAMOND", "Delhi NCR Hub", "#1 International Bestseller on habit formation and cognitive cues."},
            {"The Psychology of Money: Timeless Lessons on Wealth", "Books", "Morgan Housel", "320", "499", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=700", "Appario Books", "DIAMOND", "Mumbai Hub", "19 short stories exploring the strange ways people think about money."}
        };

        for (String[] row : baseCatalog) {
            Product p = new Product();
            p.setName(row[0]);
            p.setCategory(row[1]);
            p.setBrand(row[2]);
            p.setPrice(Double.parseDouble(row[3]));
            p.setOriginalPrice(Double.parseDouble(row[4]));
            p.setImageUrl(row[5]);
            p.setSellerStoreName(row[6]);
            p.setSellerTier(row[7]);
            p.setSellerState(row[8]);
            p.setSpecifications(row[9]);
            p.setDescription("Verified Indian merchant inventory with 100% authentic brand certification, GST invoice and hassle-free 7-day return guarantee.");
            p.setRating(4.1 + (rand.nextDouble() * 0.8));
            p.setReviewCount(65 + rand.nextInt(1200));
            catalog.add(p);
        }

        String[] sellerNames = {"Appario Retail Pvt Ltd", "Infiniti Retail (Croma)", "Cloudtail India", "Hawk Electronics Store", "Titan & Fossil Official", "Sunrise Sports India", "Wakefit Furnishings", "Appario Fresh"};
        String[] sellerTiers = {"DIAMOND", "GOLD", "SILVER"};
        String[] sellerHubs = {"Delhi NCR Hub", "Mumbai Hub", "Bengaluru Hub", "Hyderabad Hub", "Chennai Hub", "Kolkata Hub"};
        String[] editions = {"Edition 2026", "Festive Pack", "Gen-2 Upgraded", "Pro Series", "Limited Edition", "Value Combo"};

        for (int i = 1; i <= 480; i++) {
            String[] ref = baseCatalog[rand.nextInt(baseCatalog.length)];
            String edition = editions[rand.nextInt(editions.length)];
            String seller = sellerNames[rand.nextInt(sellerNames.length)];
            String tier = sellerTiers[rand.nextInt(sellerTiers.length)];
            String hub = sellerHubs[rand.nextInt(sellerHubs.length)];

            Product p = new Product();
            p.setName(ref[0] + " [" + edition + " - SKU #" + (1000 + i) + "]");
            p.setCategory(ref[1]);
            p.setBrand(ref[2]);
            
            double basePrice = Double.parseDouble(ref[3]);
            double multiplier = 0.82 + (rand.nextDouble() * 0.38);
            double price = Math.round(basePrice * multiplier);
            double mrp = Math.round(price * 1.30);

            p.setPrice(price);
            p.setOriginalPrice(mrp);
            p.setImageUrl(ref[5]);
            p.setSellerStoreName(seller);
            p.setSellerTier(tier);
            p.setSellerState(hub);
            p.setSpecifications(ref[9] + " Includes " + edition + " certified packaging.");
            p.setDescription("Official Indian marketplace distribution. FBA Fast-track dispatch directly from " + hub + ".");
            p.setRating(3.9 + (rand.nextDouble() * 1.0));
            p.setReviewCount(15 + rand.nextInt(850));
            catalog.add(p);
        }

        productRepository.saveAll(catalog);
    }
}