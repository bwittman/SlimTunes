package slimtunes.model.xml;

import java.nio.file.Path;
import java.time.LocalDateTime;

public interface WriteXML {
    void write(Writer writer);

    static void write(Writer writer, Object key, Object value) {
        String keyName = key.toString();
        if (value instanceof Integer)
            writer.keyInteger(keyName, Long.valueOf((Integer) value));
        else if(value instanceof Long)
            writer.keyInteger(keyName, (Long) value);
        else if(value instanceof LocalDateTime)
            writer.keyDate(keyName, (LocalDateTime) value);
        else if(value instanceof String)
            writer.keyString(keyName, (String) value);
        else if(value instanceof Boolean)
            writer.keyBoolean(keyName, (Boolean) value);
        else if(value instanceof Path)
            writer.keyPath(keyName, (Path)value);

        // Otherwise, nothing gets written (meaning null values)
    }

}
