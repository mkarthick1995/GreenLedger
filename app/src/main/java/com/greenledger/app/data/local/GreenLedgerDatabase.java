@Database(
    entities = {
        BusinessPartnerEntity.class,
        OrderEntity.class
        // ...existing code...
    },
    version = 1,
    exportSchema = false
)
public abstract class GreenLedgerDatabase extends RoomDatabase {
    private static volatile GreenLedgerDatabase INSTANCE;

    public abstract BusinessPartnerDao businessPartnerDao();
    public abstract OrderDao orderDao();
    // ...existing code...

    public static GreenLedgerDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GreenLedgerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GreenLedgerDatabase.class, "greenledger.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
