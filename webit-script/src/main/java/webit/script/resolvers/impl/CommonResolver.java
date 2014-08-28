// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.bean.BeanUtil;

/**
 *
 * @author Zqq
 */
public class CommonResolver implements GetResolver, SetResolver, OutResolver {

    public Object get(final Object object, final Object property) {
        try {
            return BeanUtil.get(object, String.valueOf(property));
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public void set(final Object object, final Object property, final Object value) {
        try {
            BeanUtil.set(object, String.valueOf(property), value);
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public void render(final Out out, final Object bean) {
        out.write(bean.toString());
    }

    public Class<?> getMatchClass() {
        return Object.class;
    }
}
