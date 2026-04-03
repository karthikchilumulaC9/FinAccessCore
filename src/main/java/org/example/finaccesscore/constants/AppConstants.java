package org.example.finaccesscore.constants;

public final class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Role Constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ANALYST = "ANALYST";
    public static final String ROLE_VIEWER = "VIEWER";
    
    // API Endpoints
    public static final String API_BASE = "/api";
    public static final String API_USERS = API_BASE + "/users";
    public static final String API_RECORDS = API_BASE + "/records";
    public static final String API_DASHBOARD = API_BASE + "/dashboard";
    public static final String API_AUTH = API_BASE + "/auth";
    
    // Validation Messages
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String USERNAME_SIZE = "Username must be between 3 and 50 characters";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Invalid email format";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_SIZE = "Password must be at least 8 characters long";
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_POSITIVE = "Amount must be greater than 0";
    public static final String TYPE_REQUIRED = "Type is required";
    public static final String CATEGORY_REQUIRED = "Category is required";
    public static final String DATE_REQUIRED = "Date is required";
    public static final String ROLES_REQUIRED = "Roles are required";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    
    // Date Format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MONTH_FORMAT = "yyyy-MM";
}
