package br.com.anteros.csv.bean;

import br.com.anteros.csv.AnterosCSVReader;
import br.com.anteros.exceptions.CsvRequiredFieldEmptyException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Converts CSV strings to objects.
 * Unlike CsvToBean it returns a single record at a time.
 *
 * @param <T> Class to convert the objects to.
 * @deprecated Use {@link CsvToBean#iterator()} instead.
 */
@Deprecated
public class IterableCSVToBean<T> extends AbstractCSVToBean implements Iterable<T> {
    private final MappingStrategy<T> strategy;
    private final AnterosCSVReader csvReader;
    private final CsvToBeanFilter filter;
    private boolean hasHeader;
    private Locale errorLocale = Locale.getDefault();

    /**
     * IterableCSVToBean constructor
     *
     * @param csvReader CSVReader.  Should not be null.
     * @param strategy  MappingStrategy used to map CSV data to the bean.  Should not be null.
     * @param filter    Optional CsvToBeanFilter used remove unwanted data from reads.
     */
    public IterableCSVToBean(AnterosCSVReader csvReader, MappingStrategy<T> strategy, CsvToBeanFilter filter) {
        this.csvReader = csvReader;
        this.strategy = strategy;
        this.filter = filter;
        this.hasHeader = false;
    }

    /**
     * Retrieves the MappingStrategy.
     * @return The MappingStrategy being used by the IterableCSVToBean.
     */
    protected MappingStrategy<T> getStrategy() {
        return strategy;
    }

    /**
     * Retrieves the CSVReader.
     * @return The CSVReader being used by the IterableCSVToBean.
     */
    protected AnterosCSVReader getCSVReader() {
        return csvReader;
    }

    /**
     * Retrieves the CsvToBeanFilter
     *
     * @return The CsvToBeanFilter being used by the IterableCSVToBean.
     */
    protected CsvToBeanFilter getFilter() {
        return filter;
    }

    /**
     * Reads and processes a single line.
     * @return Object of type T with the requested information or null if there
     *   are no more lines to process.
     * @throws IllegalAccessException Thrown if there is a failure in introspection.
     * @throws InstantiationException Thrown when getting the PropertyDescriptor for the class.
     * @throws IOException Thrown when there is an unexpected error reading the file.
     * @throws IntrospectionException Thrown if there is a failure in introspection.
     * @throws InvocationTargetException Thrown if there is a failure in introspection.
     * @throws CsvRequiredFieldEmptyException If a field is required, but the
     *   header or column position for the field is not present in the input
     */
    public T nextLine() throws IllegalAccessException, InstantiationException,
            IOException, IntrospectionException, InvocationTargetException,
            CsvRequiredFieldEmptyException {
        if (!hasHeader) {
            strategy.captureHeader(csvReader);
            hasHeader = true;
        }
        T bean = null;
        String[] line;
        do {
            line = csvReader.readNext();
        } while (line != null && (filter != null && !filter.allowLine(line)));
        if (line != null) {
            strategy.verifyLineLength(line.length);
            bean = strategy.createBean();
            for (int col = 0; col < line.length; col++) {
                PropertyDescriptor prop = strategy.findDescriptor(col);
                if (null != prop) {
                    String value = checkForTrim(line[col], prop);
                    Object obj = convertValue(value, prop);
                    prop.getWriteMethod().invoke(bean, obj);
                }
            }
        }
        return bean;
    }

    @Override
    protected PropertyEditor getPropertyEditor(PropertyDescriptor desc)
            throws InstantiationException, IllegalAccessException {
        Class<?> cls = desc.getPropertyEditorClass();
        if (null != cls) {
            return (PropertyEditor) cls.newInstance();
        }
        return getPropertyEditorValue(desc.getPropertyType());
    }
    
    /**
     * Sets the locale to be used for error messages.
     * @param errorLocale The locale to be used for all error messages. If null,
     *   the default locale is used.
     * @since 4.0
     */
    public void setErrorLocale(Locale errorLocale) {
        this.errorLocale = ObjectUtils.defaultIfNull(errorLocale, Locale.getDefault());
    }
    
    @Override
    public Iterator<T> iterator() {
        return iterator(this);
    }

    private Iterator<T> iterator(final IterableCSVToBean<T> bean) {
        return new Iterator<T>() {
            private T nextBean;

            @Override
            public boolean hasNext() {
                if (nextBean != null) {
                    return true;
                }

                try {
                    nextBean = bean.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return nextBean != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T holder = nextBean;
                nextBean = null;
                return holder;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(ResourceBundle.getBundle("opencsv", errorLocale).getString("read.only.iterator"));
            }
        };
    }
}
