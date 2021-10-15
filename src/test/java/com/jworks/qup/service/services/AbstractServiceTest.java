package com.jworks.qup.service.services;

import com.jworks.qup.service.config.TestConfig;
import com.jworks.qup.service.framework.TestSupport;
import java.util.Collection;
import java.util.function.Supplier;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author bodmas
 * @since Oct 4, 2021.
 */
@DataJpaTest
@Import(TestConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
// TODO: Rename to BaseServiceTest
public class AbstractServiceTest extends TestSupport {

    // Maximum number of times to attempt clearing.
    private static final int MAX_DELETE_ATTEMPTS = 2;
    private final boolean sessionInViewEnabled;

    public AbstractServiceTest() {
        this(true);
    }

    public AbstractServiceTest(boolean sessionInViewEnabled) {
        this.sessionInViewEnabled = sessionInViewEnabled;
    }

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeEach
    public void baseSetUp() {
        System.out.println("base setup");
        if (sessionInViewEnabled)
            openSessionInView();
    }

    @AfterEach
    public void baseTearDown() {
        System.out.println("base teardown");
        if (sessionInViewEnabled)
            closeSessionInView();
    }

    protected final void clear(Collection<Supplier<Boolean>> actions) {
        boolean isWithoutErrors;
        int remainingAttempts = MAX_DELETE_ATTEMPTS;
        do {
//            System.out.println("Invoking clearActions: " + remainingAttempts);
            isWithoutErrors = actions.stream().map(Supplier::get).reduce(Boolean.TRUE, Boolean::logicalAnd);
            remainingAttempts--;
        } while (!isWithoutErrors && remainingAttempts > 0);
        if (!isWithoutErrors)
            throw new RuntimeException("Unable to clear without errors after " + MAX_DELETE_ATTEMPTS + " attempts");
    }

    private void openSessionInView() throws HibernateException, IllegalStateException {
        Session session = sessionFactory.openSession();
        session.setHibernateFlushMode(FlushMode.MANUAL);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    }

    private void closeSessionInView() throws IllegalStateException {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.closeSession(sessionHolder.getSession());
    }
}
