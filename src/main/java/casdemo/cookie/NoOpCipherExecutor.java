package casdemo.cookie;

import java.io.Serializable;

import org.jasig.cas.CipherExecutor;
import org.jasig.cas.util.AbstractCipherExecutor;

public class NoOpCipherExecutor extends AbstractCipherExecutor<Serializable, String> {

	private static CipherExecutor<Serializable, String> INSTANCE;

    protected NoOpCipherExecutor() {}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CipherExecutor<Serializable, String> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoOpCipherExecutor();
        }
        return INSTANCE;
    }

    @Override
    public String encode(final Serializable value) {
        return value.toString();
    }

    @Override
    public String decode(final Serializable value) {
        return value.toString();
    }
	
}
