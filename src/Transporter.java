import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
/**
 * A transporter provides transportation service between
 * two suppliers.
 * Transporter attributes include:
 * name, source supplier, destination supplier,
 * cost per unit shipped,
 * maximum number of units that can be handled,
 * and their current allocation of units.
 *
 * @author CS4050 (design)
 * @author Dr. Jody Paul (code)
 * @version 20231112
 */
public class Transporter implements java.io.Serializable {
    /** Serialization version requirement. */
    private static final long serialVersionUID = 405002L;
    /** Default file name for serialized object. */
    private static final String SERIAL_FILENAME = "transporter.ser";

    /** Maximum number of units any transporter may ship. */
    public static final int MAX_UNITS = Integer.MAX_VALUE;

    /** Name of the transporter (unique). */
    private String name;
    
    /** Beginning location. */
    private Supplier from;
    
    /** End location. */
    private Supplier to;
    
    /** Cost per unit shipped. */
    private int costPerUnit;
    
    /** Number of units allocated to this transporter. */
    private int allocation;
    
    /** Maximum number of units that can be handled. */
    private int maxCapacity;

    /** Construct a transporter using default values. */
    public Transporter() {
        this.name = this.toString();
        this.from = null;
        this.to = null;
        this.costPerUnit = 0;
        this.allocation = 0;
        this.maxCapacity = MAX_UNITS;
    }

    /**
     * Fully parameterized Transporter constructor.
     * @param name the name of this transporter (must be unique)
     * @param from the source of goods of this transporter
     * @param to the receiver of this transporter
     * @param cost the cost per unit charged by this transporter
     * @param maximumAllocation the maximum number of units this transporter can be allocated
     * @param allocation the number of units initially allocated to this transporter
     */
    public Transporter(String name,
                       Supplier from,
                       Supplier to,
                       int cost,
                       int maximumAllocation,
                       int allocation) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.costPerUnit = cost;
        this.maxCapacity = maximumAllocation;
        if (allocation <= maximumAllocation) {
            this.allocation = allocation;
        } else {
            this.allocation = maximumAllocation;
        }
    }

    /**
     * @return the name of this transporter
     */
    public String name() {
        return this.name;
    }
    
    /**
     * @return the beginning location for shipments
     */
    public Supplier from() {
        return this.from;
    }
    
    /**
     * @return the final location for shipments
     */
    public Supplier to() {
        return this.to;
    }
    
    /**
     * @return the cost of shipping a single unit
     */
    public int costPerUnit() {
        return this.costPerUnit;
    }
    
    /**
     * @return the maximum number of units this transporter can handle
     */
    public int maxCapacity() {
        return this.maxCapacity;
    }
    
    /**
     * @return the current number of units allocated to this transporter
     */
    public int allocation() {
        return this.allocation;
    }

    /**
     * Modify the allocation of this transporter.
     * @param allocation the new allocation for this transporter
     */
    public void setAllocation(int allocation) {
        if (allocation > this.maxCapacity) {
            this.allocation = this.maxCapacity;
        } else {
            this.allocation = allocation;
        }
    }

    /**
     * Compares this transporter to the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a Transporter object with the same
     * name, from, to, cost, maximum allocation, and allocation values.
     * @param anObject the object to compare with this supplier
     * @return <code>true</code> if the given object represents a Transporter
     *         equivalent to this transporter, <code>false</code> otherwise
     */
    @Override
    public boolean equals(final Object anObject) {
        if ((anObject == null)
                || (this.getClass() != anObject.getClass())) {
            return false;
        }
        Transporter other = ((Transporter) anObject);
        return (((this.name != null) && this.name.equals(other.name))
                && ((this.from == null && other.from == null)
                    || ((this.from != null) && this.from.equals(other.from)))
                && ((this.to == null && other.to == null)
                    || ((this.to != null) && this.to.equals(other.to)))
                && this.costPerUnit == other.costPerUnit
                && this.maxCapacity == other.maxCapacity
                && this.allocation == other.allocation);
    }

    /**
     * Returns a hash code value for this transporter.
     * @return hash code value for this transporter
     */
    @Override
    public int hashCode() {
        if (name == null) return 43;
        return this.name.hashCode();
    }

    /**
     * Save this transporter to a file.
     * @param filename the name of the file in which to save this transporter;
     *                 if null, uses default file name
     * @return <code>true</code> if successful save;
     *         <code>false</code> otherwise
     * @throws java.io.IOException if unexpected IO error
     */
    public final boolean save(final String filename) throws java.io.IOException {
        boolean success = true;
        String transporterFileName = filename;
        if (transporterFileName == null) {
            transporterFileName = Transporter.SERIAL_FILENAME;
        }
        // Serialize the transporter.
        try {
            OutputStream file = new FileOutputStream(transporterFileName);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
                output.writeObject(this);
            } finally { output.close(); }
        } catch (IOException ex) {
            System.err.println("Unsuccessful save. " + ex);
            throw ex;
        }

        // Attempt to deserialize the transporter as verification.
        try {
            InputStream file = new FileInputStream(transporterFileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                @SuppressWarnings("unchecked") // Accommodate type erasure.
                Transporter restored = (Transporter) input.readObject();
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
     * Restore this transporter from a file.
     * <br /><em>Postconditions:</em>
     * <blockquote>If successful, previous contents of this transporter have
     * been replaced by the contents of the file.
     * If unsuccessful, content of the transporter is unchanged.</blockquote>
     * @param filename the name of the file from which to restore this transporter;
     *                 if null, uses default file name
     * @return <code>true</code> if successful restore;
     *         <code>false</code> otherwise
     * @throws java.io.IOException if unexpected IO error
     */
    public final boolean restore(final String filename) throws
    java.io.IOException {
        boolean success = false;
        String transporterFileName = filename;
        if (transporterFileName == null) {
            transporterFileName = Transporter.SERIAL_FILENAME;
        }
        Transporter restored = null;
        try {
            InputStream file = new FileInputStream(transporterFileName);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                @SuppressWarnings("unchecked") // Accommodate type erasure.
                
                Transporter retrieved = (Transporter) input.readObject();
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
            this.from = restored.from;
            this.to = restored.to;
            this.costPerUnit = restored.costPerUnit;
            this.maxCapacity = restored.maxCapacity;
            this.allocation = restored.allocation;
        }
        return success;
    }
}
