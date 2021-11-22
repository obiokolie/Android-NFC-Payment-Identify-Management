package com.finidata.nfcwriter;

/**
 * Created by OBINNA OKOLIE on 8/15/2017.
 */
public class DBContract {
    public static abstract class NewUserInfo
    {
        public static final String USER_FIRST_NAME = "userFirstName";
        public static final String USER_LAST_NAME = "userLastName";
        public static final String USER_EMAIL = "userEmail";
        public static final String USER_OCCUPATION = "userOccupation";
        public static final String USER_LOCATION = "userLocation";
        public static final String USER_GENDER = "userGender";
        public static final String USER_PHONE = "userPhone";
        public static final String USER_PICTURE = "userPicture";
        public static final String USER_TABLE_NAME = "userTableName";
    }

    public static abstract class TransactionInfo
    {
        public static final String TX_ID = "transactionID";
        public static final String TX_DATE = "transactionData";
        public static final String TX_ITEM = "transactionItem";
        public static final String ITEM_QUANTITY = "itemQuantity";
        public static final String ORIGINATING_DEVICE = "originatingDevice";
        public static final String USER_ID = "userID";
        public static final String TX_TABLE_NAME = "transactionTable";
    }

    public static abstract class DeviceInfo
    {
        public static final String DEVIC_ID = "transactionID";
        public static final String DEVICE_MAC = "transactionData";
        public static final String DEVICE_TABLE_NAME = "deviceTable";
    }
}
