package webit.script.core.ast.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclare implements MethodDeclare {

    private final Constructor constructor;
    private final int argsCount;

    public NativeConstructorDeclare(Constructor constructor, int argsCount) {
        this.constructor = constructor;
        this.argsCount = argsCount;
    }

    public Object execute(Context context, Object[] args) {

        Object[] methodArgs;
        if (args != null) {
            int copyLen = args.length;

            if (copyLen == argsCount) {
                methodArgs = args;
            } else {
                //TODO: Warning 参数个数不一致
                if (copyLen > argsCount) {
                    copyLen = argsCount;
                }
                methodArgs = new Object[argsCount];
                System.arraycopy(args, 0, methodArgs, 0, copyLen);
            }
        } else {
            methodArgs = new Object[argsCount];
        }
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("this a abstract class, can't create new instance: "+ ex.getLocalizedMessage());
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: "+ ex.getLocalizedMessage());
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: "+ ex.getLocalizedMessage());
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }
}