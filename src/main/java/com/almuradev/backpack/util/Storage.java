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
package com.almuradev.backpack.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Storage {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;
    private final File configuration;
    private final List<DefaultNode> defaultNodes = Lists.newArrayList();
    private final Logger logger;

    public Storage(PluginContainer container, File configuration, ConfigurationLoader<CommentedConfigurationNode> loader) {
        logger = LoggerFactory.getLogger(container.getName() + " - Storage");
        this.loader = loader;
        this.configuration = configuration;
        this.init();
    }

    /**
     * Initializes the configuration file. File is created if non-existent.
     * @return {@link Storage} for chaining.
     */
    public Storage init() {
        if (!configuration.exists()) {
            try {
                configuration.createNewFile();
            } catch (IOException e) {
                logger.error("Unable to create new configuration file!", e);
            }
        }
        try {
            rootNode = this.loader.load();
        } catch (IOException e) {
            logger.error("Unable to load configuration file!", e);
        }
        return this;
    }

    /**
     * Loads the configuration file.
     * @return {@link Storage} for chaining.
     */
    @SuppressWarnings("unchecked")
    public Storage load() {
        defaultNodes.stream().filter(entry -> entry.value != null).forEach(entry -> {
            final CommentedConfigurationNode node = getChildNode(entry.key);
            if (node.getValue() == null) {
                if (entry.token.isPresent()) {
                    try {
                        getChildNode(entry.key).setValue((TypeToken) entry.token.get(), entry.value);
                        getChildNode(entry.key).setComment(entry.comment);
                    } catch (ObjectMappingException e) {
                        logger.warn("Unable to map TypeToken!", e);
                    }
                } else {
                    getChildNode(entry.key).setValue(entry.value).setComment(entry.comment);
                }
            }
        });
        save();
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Unable to load configuration!", e);
        }
        return this;
    }

    /**
     * Saves the configuration file.
     * @return {@link Storage} for chaining.
     */
    public Storage save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            logger.error("Unable to save configuration!", e);
        }
        return this;
    }

    /**
     * Registers a default node. See {@link Storage#registerDefaultNode(String, Object, String)}.
     * @param key The key to register.
     * @param value The value to register.
     */
    public void registerDefaultNode(String key, Object value) {
        this.registerDefaultNode(key, value, null);
    }

    /**
     * Registers a default node without a {@link TypeToken}. See {@link Storage#registerDefaultNode(String, Object, String, Optional)}.
     * @param key The key to register.
     * @param value The value to register.
     * @param comment The comment to register.
     */
    public void registerDefaultNode(String key, Object value, String comment) {
        registerDefaultNode(key, value, comment, Optional.empty());
    }

    /**
     * Registers a default node. Calls {@link Storage#save()} and {@link Storage#load()}.
     * @param key The path to register.
     * <p>The key is split by a period for example "path.to.node" is the equivalent of...
     * path {
     *     to {
     *         node=""
     *     }
     * }</p>
     * @param value The value to register.
     * @param comment The comment to register.
     * @param token The optional {@link TypeToken} for the value being registered.
     */
    @SuppressWarnings("unchecked")
    public <T> void registerDefaultNode(String key, Object value, String comment, Optional<TypeToken<T>> token) {
        final String[] nodes = key.split("\\.");
        final List<String> currentPath = Lists.newArrayList();
        for (int i = 0; i < nodes.length; i++) {
            if (i < nodes.length - 1) {
                currentPath.add(i, nodes[i]);
                final String joinedPath = Joiner.on(",").skipNulls().join(currentPath).replace(",", ".");
                defaultNodes.add(new DefaultNode(joinedPath, null, null, token));
            } else {
                defaultNodes.add(new DefaultNode(key, value, comment, token));
            }
        }
        this.save();
        this.load();
    }

    /**
     * Gets the node from the root node.
     * @param path The path to the node split by periods.
     * @return The {@link CommentedConfigurationNode}.
     */
    public CommentedConfigurationNode getChildNode(String path) {
        return rootNode.getNode((Object[]) path.split("\\."));
    }

    public Object getChildValue(String path) {
        return getChildNode(path).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> T getChildValue(String path, Class<T> clazz) {
        try {
            return getChildNode(path).getValue(TypeToken.of(clazz));
        } catch (ObjectMappingException e) {
            logger.error("Unable to map object to TypeToken", e);
        }
        // Return a value even if the token doesn't successfully map
        return (T) getChildNode(path).getValue();
    }

    public static class DefaultNode<T> {

        public String key;
        public T value;
        public String comment;
        public final Optional<TypeToken<T>> token;

        public DefaultNode(String key, T value, String comment, Optional<TypeToken<T>> token) {
            this.key = key;
            this.value = value;
            this.comment = comment;
            this.token = token;
        }
    }
}