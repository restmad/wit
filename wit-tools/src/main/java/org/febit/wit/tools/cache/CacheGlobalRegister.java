// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache;

import java.io.Serializable;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;
import org.febit.wit.io.Out;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ArrayUtil;
import org.febit.wit.util.ByteArrayOutputStream;
import org.febit.wit.util.CharArrayWriter;

/**
 *
 * @author zqq90
 */
public class CacheGlobalRegister implements GlobalRegister {

    protected final static String DEFAULT_NAME = "cache";

    protected String name = DEFAULT_NAME;
    protected boolean registCacheClear = false;
    protected boolean registCacheRemove = true;

    protected CacheProvider cacheProvider;

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(name, new CacheMethodDeclare(cacheProvider));
        if (registCacheRemove) {
            manager.setConst(name + "_remove", new CacheRemoveMethodDeclare(cacheProvider));
        }
        if (registCacheClear) {
            manager.setConst(name + "_clear", new CacheClearMethodDeclare(cacheProvider));
        }
    }

    protected static class CacheClearMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheClearMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            this.cacheProvider.clear();
            return InternalContext.VOID;
        }
    }

    protected static class CacheRemoveMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheRemoveMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            this.cacheProvider.remove(ArrayUtil.get(args, 0, null));
            return InternalContext.VOID;
        }
    }

    protected static class CacheMethodDeclare implements MethodDeclare {

        protected final static Object[] EMPTY_ARRAY = new Object[0];
        protected final CacheProvider cacheProvider;

        public CacheMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(final InternalContext context, final Object[] args) {
            final int len;
            if (args == null || (len = args.length) == 0) {
                throw new ScriptRuntimeException("This method need 1 argument at least.");
            }
            final CachingEntry cachingEntry;
            final Object firstArgument = args[0];
            if (firstArgument instanceof MethodDeclare) {
                cachingEntry = buildIfAbent(context, firstArgument, (MethodDeclare) firstArgument, args, 1);
            } else if (len > 1) {
                final Object secondArgument;
                if ((secondArgument = args[1]) instanceof MethodDeclare) {
                    cachingEntry = buildIfAbent(context, firstArgument, (MethodDeclare) secondArgument, args, 2);
                } else {
                    throw new ScriptRuntimeException("This method need a function argument at index 0 or 1.");
                }
            } else {
                throw new ScriptRuntimeException("This method need a function argument.");
            }
            context.write(cachingEntry.outted);
            return cachingEntry.returned;
        }

        protected CachingEntry buildIfAbent(final InternalContext context,
                final Object key,
                final MethodDeclare methodDeclare,
                final Object[] args,
                final int argsStart) {

            CachingEntry result;
            if ((result = (CachingEntry) this.cacheProvider.get(key)) == null) {
                final Object returned;
                final Object outted;
                final Out preOut;

                final Object[] methodArgs;

                final int len = args.length;
                if (len > argsStart) {
                    final int methodArgsLen = len - argsStart;
                    System.arraycopy(args, argsStart, methodArgs = new Object[methodArgsLen], 0, methodArgsLen);
                } else {
                    methodArgs = EMPTY_ARRAY;
                }
                preOut = context.out;
                if (preOut.isByteStream()) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream(256);

                    context.out = new OutputStreamOut(out, (OutputStreamOut) preOut);

                    try {
                        returned = methodDeclare.invoke(context, methodArgs);
                    } finally {
                        context.out = preOut;
                    }
                    outted = out.toArray();
                } else {
                    final CharArrayWriter writer = new CharArrayWriter(256);

                    context.out = preOut instanceof WriterOut
                            ? new WriterOut(writer, (WriterOut) preOut)
                            : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());

                    try {
                        returned = methodDeclare.invoke(context, methodArgs);
                        outted = writer.toArray();
                    } finally {
                        context.out = preOut;
                    }
                }
                this.cacheProvider.put(key, result = new CachingEntry(returned, outted));
            }
            return result;
        }
    }

    protected static final class CachingEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        public final Object returned;
        public final Object outted;

        public CachingEntry(Object returned, Object outted) {
            this.returned = returned;
            this.outted = outted;
        }
    }
}
