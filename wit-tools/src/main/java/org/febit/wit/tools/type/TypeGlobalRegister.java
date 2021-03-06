// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.type;

import org.febit.wit.InternalContext;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class TypeGlobalRegister implements GlobalRegister {

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst("is_array", is_array);
        manager.setConst("is_bool", is_bool);
        manager.setConst("is_function", is_function);
        manager.setConst("is_callable", is_function);
        manager.setConst("is_null", is_null);
        manager.setConst("is_number", is_number);
    }

    public final static MethodDeclare is_function = new MethodDeclare() {

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof MethodDeclare;
        }
    };

    public final static MethodDeclare is_number = new MethodDeclare() {

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof Number;
        }
    };

    public final static MethodDeclare is_bool = new MethodDeclare() {

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof Boolean;
        }
    };

    public final static MethodDeclare is_null = new MethodDeclare() {

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            return args == null
                    || args.length == 0
                    || args[0] == null;
        }
    };

    public final static MethodDeclare is_array = new MethodDeclare() {

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            final Object item;
            return (item = ArrayUtil.get(args, 0, null)) != null
                    && item.getClass().isArray();
        }
    };
}
