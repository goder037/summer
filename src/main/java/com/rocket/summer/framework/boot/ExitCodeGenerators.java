package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains a collection of {@link ExitCodeGenerator} instances and allows the final exit
 * code to be calculated.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @see #getExitCode()
 * @see ExitCodeGenerator
 */
class ExitCodeGenerators implements Iterable<ExitCodeGenerator> {

    private List<ExitCodeGenerator> generators = new ArrayList<ExitCodeGenerator>();

    public void addAll(Throwable exception, ExitCodeExceptionMapper... mappers) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mappers, "Mappers must not be null");
        addAll(exception, Arrays.asList(mappers));
    }

    public void addAll(Throwable exception,
                       Iterable<? extends ExitCodeExceptionMapper> mappers) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mappers, "Mappers must not be null");
        for (ExitCodeExceptionMapper mapper : mappers) {
            add(exception, mapper);
        }
    }

    public void add(Throwable exception, ExitCodeExceptionMapper mapper) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mapper, "Mapper must not be null");
        add(new MappedExitCodeGenerator(exception, mapper));
    }

    public void addAll(ExitCodeGenerator... generators) {
        Assert.notNull(generators, "Generators must not be null");
        addAll(Arrays.asList(generators));
    }

    public void addAll(Iterable<? extends ExitCodeGenerator> generators) {
        Assert.notNull(generators, "Generators must not be null");
        for (ExitCodeGenerator generator : generators) {
            add(generator);
        }
    }

    public void add(ExitCodeGenerator generator) {
        Assert.notNull(generator, "Generator must not be null");
        this.generators.add(generator);
    }

    @Override
    public Iterator<ExitCodeGenerator> iterator() {
        return this.generators.iterator();
    }

    /**
     * Get the final exit code that should be returned based on all contained generators.
     * @return the final exit code.
     */
    public int getExitCode() {
        int exitCode = 0;
        for (ExitCodeGenerator generator : this.generators) {
            try {
                int value = generator.getExitCode();
                if (value > 0 && value > exitCode || value < 0 && value < exitCode) {
                    exitCode = value;
                }
            }
            catch (Exception ex) {
                exitCode = (exitCode != 0) ? exitCode : 1;
                ex.printStackTrace();
            }
        }
        return exitCode;
    }

    /**
     * Adapts an {@link ExitCodeExceptionMapper} to an {@link ExitCodeGenerator}.
     */
    private static class MappedExitCodeGenerator implements ExitCodeGenerator {

        private final Throwable exception;

        private final ExitCodeExceptionMapper mapper;

        MappedExitCodeGenerator(Throwable exception, ExitCodeExceptionMapper mapper) {
            this.exception = exception;
            this.mapper = mapper;
        }

        @Override
        public int getExitCode() {
            return this.mapper.getExitCode(this.exception);
        }

    }

}

