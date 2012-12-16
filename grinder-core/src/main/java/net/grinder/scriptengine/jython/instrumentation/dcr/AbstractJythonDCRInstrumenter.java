// Copyright (C) 2011 - 2012 Philip Aston
// All rights reserved.
//
// This file is part of The Grinder software distribution. Refer to
// the file LICENSE which is part of The Grinder distribution for
// licensing details. The Grinder distribution is available on the
// Internet at http://grinder.sourceforge.net/
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package net.grinder.scriptengine.jython.instrumentation.dcr;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.grinder.script.NonInstrumentableTypeException;
import net.grinder.script.Test.InstrumentationFilter;
import net.grinder.scriptengine.AbstractDCRInstrumenter;
import net.grinder.scriptengine.DCRContext;
import net.grinder.scriptengine.Recorder;
import net.grinder.util.weave.ClassSource;
import net.grinder.util.weave.ParameterSource;

import org.python.core.PyClass;
import org.python.core.PyFunction;
import org.python.core.PyInstance;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyProxy;
import org.python.core.PyReflectedConstructor;
import org.python.core.PyReflectedFunction;


/**
 * Common code used by the Jython DCR instrumenters.
 *
 * @author Philip Aston
 */
abstract class AbstractJythonDCRInstrumenter extends AbstractDCRInstrumenter {

  /**
   * Constructor.
   *
   * @param context The DCR context.
   */
  protected AbstractJythonDCRInstrumenter(final DCRContext context) {
    super(context);
  }

  /**
   * Extract and instrument the underlying Java methods for instrumentation.
   *
   * @param <T>
   *          Constructor<?> or Method
   * @param pyReflectedFunction
   *          The Jython object.
   * @return A list of Java methods or constructors.
   * @throws NonInstrumentableTypeException
   *           If the methods could not be extracted.
   */
  @SuppressWarnings("unchecked")
  private <T extends Member> List<T>
    extractJavaMethods(final PyReflectedFunction pyReflectedFunction)
    throws NonInstrumentableTypeException {

    // ReflectedArgs is package scope in Jython 2.2.1; use reflection
    // to avoid compilation issues.

    final Object[] argsList = pyReflectedFunction.argslist;
    final int nargs = pyReflectedFunction.nargs;

    final List<T> result = new ArrayList<T>(nargs);

    for (int i = 0; i < nargs; ++i) {
      final Object argument = argsList[i];

      try {
        final Field dataField = argument.getClass().getField("data");
        dataField.setAccessible(true);
        result.add((T)dataField.get(argument));
      }
      catch (final Exception e) {
        throw new NonInstrumentableTypeException(
          e.getMessage() + " [" + pyReflectedFunction + "]",
          e);
      }
    }

    return result;
  }

  @Override protected boolean instrument(final Object target,
                                         final Recorder recorder,
                                         final InstrumentationFilter filter)
    throws NonInstrumentableTypeException {

    if (target instanceof PyObject) {
      disallowSelectiveFilter(filter);

      // Jython object.
      if (target instanceof PyInstance) {
        transform(recorder, (PyInstance)target);
      }
      else if (target instanceof PyFunction) {
        transform(recorder, (PyFunction)target);
      }
      else if (target instanceof PyMethod) {
        final PyMethod pyMethod = (PyMethod)target;

        // PyMethod is used for bound and unbound Python methods, and
        // bound Java methods.

        if (pyMethod.im_func instanceof PyReflectedFunction) {

          // Its Java.

          // Its possible im_func might be an unbound Java method or a Java
          // constructor, but I can't find a way to trigger this. We always
          // receive a PyReflectedMethod or PyReflectedConstructor directly.
          // Here, we defensively cope with unbound methods, but not
          // constructors.
          transform(recorder,
                    (PyReflectedFunction)pyMethod.im_func,
                    pyMethod.im_self.__tojava__(Object.class));
        }
        else {
          transform(recorder, pyMethod);
        }
      }
      else if (target instanceof PyClass) {
        transform(recorder, (PyClass)target);
      }
      else if (target instanceof PyReflectedConstructor) {
        transform(recorder, (PyReflectedConstructor)target);
      }
      else if (target instanceof PyReflectedFunction) {
        transform(recorder, (PyReflectedFunction)target, null);
      }
      else {
        // Fail, rather than guess a generic approach.

        // We should never be called with a PyType, since it will be
        // converted to a PyClass or Java class by the implicit __tojava__()
        // calls as part of dispatching to the record() implementation.

        // Similarly PyObjectDerived will be converted to a Java class.

        throw new NonInstrumentableTypeException("Unknown PyObject:" +
                                                 target.getClass());
      }
    }
    else if (target instanceof PyProxy) {
      disallowSelectiveFilter(filter);

      transform(recorder, (PyProxy)target);
    }
    else {
      // Let the Java instrumenter have a go.
      return false;
    }

    return true;
  }

  private void disallowSelectiveFilter(final InstrumentationFilter filter)
    throws NonInstrumentableTypeException {

    if (filter != ALL_INSTRUMENTATION) {
      throw new NonInstrumentableTypeException(
        "The Jython instrumenters do not support selective instrumenters");
    }
  }

  protected abstract void transform(Recorder recorder, PyInstance target)
    throws NonInstrumentableTypeException;

  protected abstract void transform(Recorder recorder, PyFunction target)
    throws NonInstrumentableTypeException;

  protected abstract void transform(Recorder recorder, PyClass target)
    throws NonInstrumentableTypeException;

  protected abstract void transform(Recorder recorder, PyProxy target)
    throws NonInstrumentableTypeException;

  protected abstract void transform(Recorder recorder, PyMethod target)
    throws NonInstrumentableTypeException;

  protected final void transform(final Recorder recorder,
                                 final PyReflectedFunction target,
                                 final Object instance)
    throws NonInstrumentableTypeException {

    final List<Method> reflectedArguments = extractJavaMethods(target);

    if (instance != null) {
      for (final Method m : reflectedArguments) {

        Class<?> c = instance.getClass();

        // We want the instance's implementation, not the interface or
        // superclass method used by the call site.
        do {
          try {
            getContext().add(instance,
                             c.getDeclaredMethod(m.getName(),
                                                 m.getParameterTypes()),
                             ParameterSource.FIRST_PARAMETER,
                             recorder);
            break;
          }
          catch (final NoSuchMethodException e) {
            c = c.getSuperclass();
          }
        }
        while (c != null);
      }
    }
    else {
      for (final Method m : reflectedArguments) {
        getContext().add(m.getDeclaringClass(),
                         m,
                         ClassSource.INSTANCE, recorder);
      }
    }
  }

  protected final void transform(final Recorder recorder,
                                 final PyReflectedConstructor target)
    throws NonInstrumentableTypeException {

    final List<Constructor<?>> reflectedArguments = extractJavaMethods(target);

    for (final Constructor<?> c : reflectedArguments) {
      getContext().add(c.getDeclaringClass(), c, recorder);
    }
  }

  protected final void instrumentPublicMethodsByName(
                                       final Object target,
                                       final String methodName,
                                       final Recorder recorder,
                                       final boolean includeSuperClassMethods)
    throws NonInstrumentableTypeException {
    instrumentPublicMethodsByName(target.getClass(),
                                  target,
                                  methodName,
                                  ParameterSource.FIRST_PARAMETER,
                                  recorder,
                                  includeSuperClassMethods);
  }

  protected final void instrumentPublicMethodsByName(
                                       final Class<?> targetClass,
                                       final Object target,
                                       final String methodName,
                                       final ParameterSource targetSource,
                                       final Recorder recorder,
                                       final boolean includeSuperClassMethods)
    throws NonInstrumentableTypeException {

    // getMethods() includes superclass methods.
    for (final Method method : targetClass.getMethods()) {
      if (!includeSuperClassMethods &&
          targetClass != method.getDeclaringClass()) {
        continue;
      }

      if (!method.getName().equals(methodName)) {
        continue;
      }

      if (!targetSource.canApply(method)) {
        continue;
      }

      getContext().add(target, method, targetSource, recorder);
    }
  }
}
