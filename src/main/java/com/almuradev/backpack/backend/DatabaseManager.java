/*
 * This file is part of Backpack, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.almuradev.backpack.backend;

import com.almuradev.backpack.backend.entity.Backpacks;
import com.almuradev.backpack.backend.entity.Slots;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.nio.file.Path;

public class DatabaseManager {

    // DO NOT CHANGE THESE UNLESS I STATE OTHERWISE -- Zidane
    public static final String CONNECTION_PROVIDER = "org.hibernate.hikaricp.internal.HikariCPConnectionProvider";
    public static final String DIALECT = "org.hibernate.dialect.H2Dialect";
    public static final String DRIVER_CLASSPATH = "org.h2.jdbcx.JdbcDataSource";
    public static final String DATA_SOURCE_PREFIX = "jdbc:h2:";
    public static final String DATA_SOURCE_SUFFIX = ";AUTO_SERVER=TRUE";
    // ONLY SET THIS TO CREATE WHILE IN DEV, OTHERWISE THIS MUST BE UPDATE -- Zidane
    public static final String AUTO_SCHEMA_MODE = "create";

    private static SessionFactory sessionFactory;

    public static void init(Path databaseRootPath, String name) {
        final Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.provider_class", CONNECTION_PROVIDER);
        configuration.setProperty("hibernate.dialect", DIALECT);
        configuration.setProperty("hibernate.hikari.dataSourceClassName", DRIVER_CLASSPATH);
        configuration.setProperty("hibernate.hikari.dataSource.url",
                DATA_SOURCE_PREFIX + databaseRootPath.toString() + File.separator + name + DATA_SOURCE_SUFFIX);
        configuration.setProperty("hibernate.hbm2ddl.auto", AUTO_SCHEMA_MODE);
        registerTables(configuration);
        sessionFactory = configuration.buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
    }

    private static void registerTables(Configuration configuration) {
        // Add tables here
        configuration.addAnnotatedClass(Backpacks.class);
        configuration.addAnnotatedClass(Slots.class);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
