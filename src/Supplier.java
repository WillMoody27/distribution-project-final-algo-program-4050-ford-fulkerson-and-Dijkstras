import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
/**
 * A supplier may be a manufacturer, distributor, or depot.
 * Supplier attributes include:
 * name, demand, inventory on hand, maximum storage capacity,
 * cost per unit stored, and surplus inventory.
 *
 * @author CS4050 (design)
 * @author Dr. Jody Paul (code)
 * @version 20231112
 */
public class Supplier implements java.io.Serializable {
    /** Serialization version requirement. */
    private static final long serialVersionUID = 405002L;
    /** Default file name for serialized object. */
    private static final String SERIAL_FILENAME = "supplier.ser";

    /** Default maximum capacity. */
    public static final int MAX_CAPACITY = Integer.MAX_VALUE;

    /** Name of supplier. */
    private String name;

    /** Cost of storage at supplier's location. */
    private int storageCost;
    
    /**
     * Direct demand at this supplier's location.
     * If direct demand == 0, then is considered a depot.
     * If direct demand is less than 0, then is considered a manufacturer.
     */
    private int demand;

    /** Inventory on hand. */
    private int inventory;

    /** Maximum amount of inventory that can be held. */
    private int maxCapacity;
    
    /**
     * Surplus, defined as (inventory - demand).
     * If positive, surplus represents the number of
     * units subject to storage cost.
     * If negative, surplus represents unrealized
     * value (units that would otherwise have been sold).
     * Storing this value is not necessary because the accessor,
     * surplus(), computes this value dynamically.
     */
    private int surplus;

    /**
     * Construct a supplier using default values.
     */
    public Supplier() {
        this.name = this.toString();
        this.storageCost = 0;
        this.demand = 0;
        this.inventory = 0;
        this.maxCapacity = MAX_CAPACITY;
        this.surplus = 0;
    }

    /**
     * Fully-parameterized supplier constructor.
     * Note that surplus is automatically calculated as the
     * difference between inventory and demand.
     * @param name the name of this supplier
     * @param cost the storage cost per unit stored
     * @param demand the expected demand at this distributing supplier
     * @param inventory the actual inventory at this supplier
     * @param capacity the maximum capacity at this supplier
     */
    public Supplier(String name,
                    int cost,
                    int demand,
                    int inventory,
                    int capacity) {
        this.name = name;
        this.storageCost = cost;
        this.demand = demand;
        this.maxCapacity = capacity;
        if (inventory > capacity) {
            this.inventory = capacity;
        } else {
            this.inventory = inventory;
        }
        this.surplus = inventory - demand;
    }

    /**
     * @return this supplier's name
     */
    public String name() { return this.name; }
    
    /**
     * @return the cost of storage at this supplier's location
     */
    public int storageCost() { return this.storageCost; }

    /**
     * @return the maximum number of units that can be stored
     */
    public int maxCapacity() { return this.maxCapacity; }
    
    /**
     * @return the demand at this supplier's location.
     */
    public int demand() { return this.demand; }
    
    /**
     * @return the current inventory at this supplier's location.
     */
    public int inventory() { return this.inventory; }
    
    /**
     * @return the surplus inventory at this supplier's location.
     */
    public int surplus() {
        this.surplus = this.inventory - this.demand;
        return this.surplus;
    }


    /**
     * Compares this supplier to the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a Supplier object with the same
     * name, storage cost, demand, and inventory values.
     * @param anObject the object to compare with this supplier
     * @return <code>true</code> if the given object represents a Supplier
     *         equivalent to this supplier, <code>false</code> otherwise
     */
    @Override
    public boolean equals(final Object anObject) {
        if ((anObject == null)
                || (this.getClass() != anObject.getClass())) {
            return false;
        }
        Supplier other = ((Supplier) anObject);
        return (this.name.equals(other.name)
                && this.storageCost == other.storageCost
                && this.demand == other.demand
                && this.maxCapacity == other.maxCapacity
                && this.inventory == other.inventory);
    }

    /**
     * Returns a hash code value for this supplier.
     * @return hash code value for this supplier
     */
    @Override
    public int hashCode() {
        if (name == null) return 43;
        return this.name.hashCode();
    }

    /**
     * Save this supplier to a file.
     * @param filename the name of the file in which to save this supplier;
     *                 if null, uses default file name
     * @return <code>true</code> if successful save;
     *         <code>false</code> otherwise
     * @throws java.io.IOException if unexpected IO error
     */
    public final boolean save(final String filename) throws java.io.IOException {
        boolean success = true;
        String supplierFileName = filename;
        if (supplierFileName == null) {
            supplierFileName = Supplier.SERIAL_FILENAME;
        }
        // Serialize the supplier.
        try {
            OutputStream file = new FileOutputStream(supplierFileName);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
                output.writeObject(this);
            } finally { output.close(); }
        } catch (IOException ex) {
            System.err.println("Unsuccessful save. " + ex);
            throw ex;
        }

        // Attempt to deserialize the supplier as verification.
        try {
            InputStream file = new FileInputStream(supplierFileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                @SuppressWarnings("unchecked") // Accommodate type erasure.
                Supplier restored = (Supplier) input.readObject();
                // Simple check that deserialized data matches original.
                if (!this.toString().equals(restored.toString())) {
                    System.err.println("[1] State restore did not match save!");
                    success = false;
                }
                if (!this.equals(restored)) {
                    System.err.println("[2] State restore did not match save!");
                    success = false;
                }
            } finally { input.close(); }
        } catch (ClassNotFoundException ex) {
            System.err.println(
                "Unsuccessful deserialization: Class not found. " + ex);
            success = false;
        } catch (IOException ex) {
            System.err.println("Unsuccessful deserialization: " + ex);
            success = false;
        }
        return success;
    }

    /**
     * Restore this supplier from a file.
     * <br /><em>Postconditions:</em>
     * <blockquote>If successful, previous contents of this supplier have
     * been replaced by the contents of the file.
     * If unsuccessful, content of the supplier is unchanged.</blockquote>
     * @param filename the name of the file from which to restore this supplier;
     *                 if null, uses default file name
     * @return <code>true</code> if successful restore;
     *         <code>false</code> otherwise
     * @throws java.io.IOException if unexpected IO error
     */
    public final boolean restore(final String filename) throws
    java.io.IOException {
        boolean success = false;
        String supplierFileName = filename;
        if (supplierFileName == null) {
            supplierFileName = Supplier.SERIAL_FILENAME;
        }
        Supplier restored = null;
        try {
            InputStream file = new FileInputStream(supplierFileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                @SuppressWarnings("unchecked") // Accommodate type erasure.
                Supplier retrieved = (Supplier) input.readObject();
                restored = retrieved;
            } finally {
                input.close();
                success = true;
            }
        } catch (ClassNotFoundException ex) {
            System.err.println(
                "Unsuccessful deserialization: Class not found. " + ex);
            success = false;
        } catch (IOException ex) {
            System.err.println("Unsuccessful deserialization: " + ex);
            throw ex;
        }
        if (restored == null) {
            System.err.println(
                "Unsuccessful deserialization: restored == null");
            success = false;
        } else {
            this.name = restored.name;
            this.storageCost = restored.storageCost;
            this.demand = restored.demand;
            this.inventory = restored.inventory;
            this.maxCapacity = restored.maxCapacity;
            this.surplus = restored.surplus;
        }
        return success;
    }

}
