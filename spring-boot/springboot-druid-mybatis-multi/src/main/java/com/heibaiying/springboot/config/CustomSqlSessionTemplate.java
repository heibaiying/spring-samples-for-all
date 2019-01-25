package com.heibaiying.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.*;

/***
 * @description: 自定义 sqlSessionTemplate 实现
 * 这个方法继承自sqlSessionTemplate,主要是重写了getSqlSessionFactory方法,并在getSqlSession时候传入我们用此方法得到的SqlSessionFactory
 */

@Slf4j
public class CustomSqlSessionTemplate extends SqlSessionTemplate {
/*


    private final SqlSessionFactory sqlSessionFactory;
    private final ExecutorType executorType;
    private final SqlSession sqlSessionProxy;
    private final PersistenceExceptionTranslator exceptionTranslator;

    private Map<Object, SqlSessionFactory> targetSqlSessionFactories;
    private SqlSessionFactory defaultTargetSqlSessionFactory;


    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource(), true));
    }

    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                    PersistenceExceptionTranslator exceptionTranslator) {

        super(sqlSessionFactory, executorType, exceptionTranslator);

        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.exceptionTranslator = exceptionTranslator;

        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor());

        this.defaultTargetSqlSessionFactory = sqlSessionFactory;
    }

    public void setTargetSqlSessionFactories(Map<Object, SqlSessionFactory> targetSqlSessionFactories) {
        this.targetSqlSessionFactories = targetSqlSessionFactories;
    }

    public void setDefaultTargetSqlSessionFactory(SqlSessionFactory defaultTargetSqlSessionFactory) {
        this.defaultTargetSqlSessionFactory = defaultTargetSqlSessionFactory;
    }

    */
/***
     *  获取当前使用数据源对应的会话工厂
     *//*

    @Override
    public SqlSessionFactory getSqlSessionFactory() {

        String dataSourceKey = DataSourceContextHolder.getDataKey();
        log.info("SqlSessionFactory key : {}",dataSourceKey);
        SqlSessionFactory targetSqlSessionFactory = targetSqlSessionFactories.get(dataSourceKey);
        if (targetSqlSessionFactory != null) {
            return targetSqlSessionFactory;
        } else if (defaultTargetSqlSessionFactory != null) {
            return defaultTargetSqlSessionFactory;
        } else {
            Assert.notNull(targetSqlSessionFactories, "Property 'targetSqlSessionFactories' or 'defaultTargetSqlSessionFactory' are required");
            Assert.notNull(defaultTargetSqlSessionFactory, "Property 'defaultTargetSqlSessionFactory' or 'targetSqlSessionFactories' are required");
        }
        return this.sqlSessionFactory;
    }


    */
/**
     * 这个方法的实现和父类的实现是基本一致的，唯一不同的就是在getSqlSession方法传参中获取会话工厂的方式
     *//*

    private class SqlSessionInterceptor implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //在getSqlSession传参时候，用我们重写的getSqlSessionFactory获取当前数据源对应的会话工厂
            final SqlSession sqlSession = getSqlSession(
                    CustomSqlSessionTemplate.this.getSqlSessionFactory(),
                    CustomSqlSessionTemplate.this.executorType,
                    CustomSqlSessionTemplate.this.exceptionTranslator);
            try {
                Object result = method.invoke(sqlSession, args);
                if (!isSqlSessionTransactional(sqlSession, CustomSqlSessionTemplate.this.getSqlSessionFactory())) {
                    sqlSession.commit(true);
                }
                return result;
            } catch (Throwable t) {
                Throwable unwrapped = unwrapThrowable(t);
                if (CustomSqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                    Throwable translated = CustomSqlSessionTemplate.this.exceptionTranslator
                            .translateExceptionIfPossible((PersistenceException) unwrapped);
                    if (translated != null) {
                        unwrapped = translated;
                    }
                }
                throw unwrapped;
            } finally {
                closeSqlSession(sqlSession, CustomSqlSessionTemplate.this.getSqlSessionFactory());
            }
        }
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return this.exceptionTranslator;
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> T selectOne(String statement) {
        return this.sqlSessionProxy.<T> selectOne(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return this.sqlSessionProxy.<T> selectOne(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, mapKey);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey, rowBounds);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return this.sqlSessionProxy.selectCursor(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return this.sqlSessionProxy.selectCursor(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <E> List<E> selectList(String statement) {
        return this.sqlSessionProxy.<E> selectList(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.sqlSessionProxy.<E> selectList(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.<E> selectList(statement, parameter, rowBounds);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void select(String statement, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, handler);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, handler);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int insert(String statement) {
        return this.sqlSessionProxy.insert(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int insert(String statement, Object parameter) {
        return this.sqlSessionProxy.insert(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int update(String statement) {
        return this.sqlSessionProxy.update(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int update(String statement, Object parameter) {
        return this.sqlSessionProxy.update(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int delete(String statement) {
        return this.sqlSessionProxy.delete(statement);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public int delete(String statement, Object parameter) {
        return this.sqlSessionProxy.delete(statement, parameter);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public void clearCache() {
        this.sqlSessionProxy.clearCache();
    }

    */
/**
     * {@inheritDoc}
     *
     *//*

    @Override
    public Configuration getConfiguration() {
        return this.sqlSessionFactory.getConfiguration();
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    */
/**
     * {@inheritDoc}
     *
     * @since 1.0.2
     *
     *//*

    @Override
    public List<BatchResult> flushStatements() {
        return this.sqlSessionProxy.flushStatements();
    }

    @Override
    public void destroy() throws Exception {
        //This method forces spring disposer to avoid call of SqlSessionTemplate.close() which gives UnsupportedOperationException
    }
*/
private final SqlSessionFactory sqlSessionFactory;
    private final ExecutorType executorType;
    private final SqlSession sqlSessionProxy;
    private final PersistenceExceptionTranslator exceptionTranslator;

    private Map<Object, SqlSessionFactory> targetSqlSessionFactorys;
    private SqlSessionFactory defaultTargetSqlSessionFactory;

    public void setTargetSqlSessionFactorys(Map<Object, SqlSessionFactory> targetSqlSessionFactorys) {
        this.targetSqlSessionFactorys = targetSqlSessionFactorys;
    }

    public void setDefaultTargetSqlSessionFactory(SqlSessionFactory defaultTargetSqlSessionFactory) {
        this.defaultTargetSqlSessionFactory = defaultTargetSqlSessionFactory;
    }

    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource(), true));
    }

    public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                    PersistenceExceptionTranslator exceptionTranslator) {

        super(sqlSessionFactory, executorType, exceptionTranslator);

        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.exceptionTranslator = exceptionTranslator;

        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[] { SqlSession.class },
                new SqlSessionInterceptor());

        this.defaultTargetSqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public SqlSessionFactory getSqlSessionFactory() {

        String dataSourceKey = DataSourceContextHolder.getDataKey();
        System.out.println("当前会话工厂 : "+dataSourceKey);
        SqlSessionFactory targetSqlSessionFactory = targetSqlSessionFactorys.get(dataSourceKey);
        if (targetSqlSessionFactory != null) {
            return targetSqlSessionFactory;
        } else if (defaultTargetSqlSessionFactory != null) {
            return defaultTargetSqlSessionFactory;
        } else {
            Assert.notNull(targetSqlSessionFactorys, "Property 'targetSqlSessionFactorys' or 'defaultTargetSqlSessionFactory' are required");
            Assert.notNull(defaultTargetSqlSessionFactory, "Property 'defaultTargetSqlSessionFactory' or 'targetSqlSessionFactorys' are required");
        }
        return this.sqlSessionFactory;
    }

    @Override
    public Configuration getConfiguration() {
        return this.getSqlSessionFactory().getConfiguration();
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return this.exceptionTranslator;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T selectOne(String statement) {
        return this.sqlSessionProxy.<T> selectOne(statement);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T selectOne(String statement, Object parameter) {
        return this.sqlSessionProxy.<T> selectOne(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, mapKey);
    }

    /**
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey);
    }

    /**
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey, rowBounds);
    }

    /**
     * {@inheritDoc}
     */
    public <E> List<E> selectList(String statement) {
        return this.sqlSessionProxy.<E> selectList(statement);
    }

    /**
     * {@inheritDoc}
     */
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.sqlSessionProxy.<E> selectList(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.<E> selectList(statement, parameter, rowBounds);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, handler);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, handler);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    /**
     * {@inheritDoc}
     */
    public int insert(String statement) {
        return this.sqlSessionProxy.insert(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int insert(String statement, Object parameter) {
        return this.sqlSessionProxy.insert(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public int update(String statement) {
        return this.sqlSessionProxy.update(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int update(String statement, Object parameter) {
        return this.sqlSessionProxy.update(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public int delete(String statement) {
        return this.sqlSessionProxy.delete(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int delete(String statement, Object parameter) {
        return this.sqlSessionProxy.delete(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void clearCache() {
        this.sqlSessionProxy.clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    /**
     * {@inheritDoc}
     * @since 1.0.2
     */
    public List<BatchResult> flushStatements() {
        return this.sqlSessionProxy.flushStatements();
    }

    /**
     * Proxy needed to route MyBatis method calls to the proper SqlSession got from Spring's Transaction Manager It also
     * unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to pass a {@code PersistenceException} to
     * the {@code PersistenceExceptionTranslator}.
     */
    private class SqlSessionInterceptor implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final SqlSession sqlSession = getSqlSession(
                    CustomSqlSessionTemplate.this.getSqlSessionFactory(),
                    CustomSqlSessionTemplate.this.executorType,
                    CustomSqlSessionTemplate.this.exceptionTranslator);
            try {
                Object result = method.invoke(sqlSession, args);
                if (!isSqlSessionTransactional(sqlSession, CustomSqlSessionTemplate.this.getSqlSessionFactory())) {
                    // force commit even on non-dirty sessions because some databases require
                    // a commit/rollback before calling close()
                    sqlSession.commit(true);
                }
                return result;
            } catch (Throwable t) {
                Throwable unwrapped = unwrapThrowable(t);
                if (CustomSqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                    Throwable translated = CustomSqlSessionTemplate.this.exceptionTranslator
                            .translateExceptionIfPossible((PersistenceException) unwrapped);
                    if (translated != null) {
                        unwrapped = translated;
                    }
                }
                throw unwrapped;
            } finally {
                closeSqlSession(sqlSession, CustomSqlSessionTemplate.this.getSqlSessionFactory());
            }
        }
    }

}
