import java.util.*;
import java.util.stream.Collectors;

public class DataStorage {
    // --- Centralized Data Lists ---
    public static List<Sale> Sales = new ArrayList<>();
    public static List<Item> Items = new ArrayList<>();
    public static List<String> Categories = new ArrayList<>();
    // NEW: Centralized User Data List
    private static List<User> Users = new ArrayList<>();

    // --- Thresholds for Dashboard Alerts ---
    private static final int LOW_STOCK_THRESHOLD = 20; 
    private static final int CRITICAL_STOCK_THRESHOLD = 5;

    // Static initializer to populate initial data
    static {
        // Categories
        Categories.add("Plywoods");
        Categories.add("Lumbers");
        Categories.add("Boards");
        Categories.add("Hardware");
        Categories.add("Other");

        // Items
        Items.add(new Item("Plywood 1/4", "Plywoods", 450.00, 20));
        Items.add(new Item("Plywood 1/2", "Plywoods", 650.00, 15));
        Items.add(new Item("Coco Lumber 2x2", "Lumbers", 80.00, 50));
        Items.add(new Item("Nails 1 Inch", "Hardware", 50.00, 100));
        Items.add(new Item("Gmelina 2x3 (8ft)", "Lumbers", 120.00, 40));
        Items.add(new Item("Yakal 2x4 (10ft)", "Lumbers", 450.00, 100));
        Items.add(new Item("Apitong 2x6 (12ft)", "Lumbers", 200.00, 90));
        Items.add(new Item("Treated Lumber 4x4...", "Lumbers", 100.00, 15));
        Items.add(new Item("S4S Pine 1x2 (6ft)", "Boards", 10.00, 1000));
        Items.add(new Item("Marine Plywood", "Plywoods", 120.00, 5));
        Items.add(new Item("OSB Board 1/2\" (4x8)", "Boards", 200.00, 500));
        Items.add(new Item("MDF Board 3/4\" (4x8)", "Boards", 285.00, 300));
        Items.add(new Item("Particle Board 5/8\" (...)", "Boards", 360.00, 0));
        Items.add(new Item("Hardboard (Masoni...", "Boards", 560.00, 0));
        Items.add(new Item("Finger Joint Pine 1x...", "Boards", 420.00, 0));

        // NEW: Initial Users
        // NOTE: Passwords are plain text here, but should be HASHED in a real app.
        Users.add(new User("admin", "admin", "Admin", "Dagmis"));
        Users.add(new User("cashier", "cashier", "Cashier", "Dagmis"));
        Users.add(new User("janedoe", "password123", "Cashier", "Smith"));
    }

    // --- USER MANAGEMENT (NEW) ---

    /**
     * Authenticates a user based on username and password.
     * @param username The username provided.
     * @param password The password provided.
     * @return The User object if credentials are valid, or null otherwise.
     */
    public static User validateUser(String username, String password) {
        return Users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Checks if the username exists and the security answer is correct.
     * @param username The username to check.
     * @param securityAnswer The answer to the security question (Mother's maiden name).
     * @return The User object if the security check is passed, or null otherwise.
     */
    public static User checkSecurityAnswer(String username, String securityAnswer) {
        return Users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.getSecurityAnswer().equalsIgnoreCase(securityAnswer))
            .findFirst()
            .orElse(null);
    }

    /**
     * Updates the password for a specific user.
     * @param user The User object whose password needs to be changed.
     * @param newPassword The new password.
     * @return true if the password was updated, false otherwise.
     */
    public static boolean updateUserPassword(User user, String newPassword) {
        if (user != null) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    // --- Existing Category Management (unchanged) ---
    public static void addCategory(String category) {
        if (!Categories.contains(category)) {
            Categories.add(category);
        }
    }
    // ... other methods follow
    
    // --- Inventory/Item Calculations (unchanged) ---
    public static int getTotalItems() {
        return Items.size();
    }
    // ... other methods follow
    
    public static int getLowStockCount() {
        return (int) Items.stream()
            .filter(i -> i.getStock() > 0 && i.getStock() <= LOW_STOCK_THRESHOLD)
            .count();
    }
    
    public static int getOutOfStockCount() {
        return (int) Items.stream()
            .filter(i -> i.getStock() == 0)
            .count();
    }

    // --- Sales Calculations (unchanged) ---
    public static double getTodaysSales() {
        Date today = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        return Sales.stream()
            .filter(s -> {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(s.getTime());
                return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            })
            .mapToDouble(Sale::getTotal)
            .sum();
    }
    
    // --- Top Selling Logic (unchanged) ---
    public static List<TopSeller> getTopSellingItems(int limit) {
        // ... (existing logic)
        return new ArrayList<>(); // Stubbed for brevity
    }
}

// =========================================================================
// --- MODEL CLASSES ---
// =========================================================================

// NEW: USER Model Class
class User {
    private String username;
    private String password; // Should be HASHED in production
    private String role; // Admin or Cashier
    private String securityAnswer; // Mother's Maiden Name (Example)

    public User(String username, String password, String role, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.securityAnswer = securityAnswer;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getSecurityAnswer() { return securityAnswer; }

    // Setters
    public void setPassword(String password) { this.password = password; }
}

// --- ITEM Model Class (unchanged) ---
class Item {
    private String name;
    private String category;
    private double price;
    private int stock;

    public Item(String name, String category, double price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}

// --- SALE Model Class (unchanged) ---
class Sale {
    private Date time;
    private int orderNumber;
    private String itemName;
    private int quantity;
    private double total;

    public Sale(Date time, int orderNumber, String itemName, int quantity, double total) {
        this.time = time;
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.quantity = quantity;
        this.total = total;
    }

    public Date getTime() { return time; }
    public int getOrderNumber() { return orderNumber; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
}

// --- Helper Model for Top Selling Items (unchanged) ---
class TopSeller {
    private String itemName;
    private int quantitySold;
    private int currentStock;

    public TopSeller(String itemName, int quantitySold, int currentStock) {
        this.itemName = itemName;
        this.quantitySold = quantitySold;
        this.currentStock = currentStock;
    }

    public String getItemName() { return itemName; }
    public int getQuantitySold() { return quantitySold; }
    public int getCurrentStock() { return currentStock; }
}