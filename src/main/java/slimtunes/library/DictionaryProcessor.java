package slimtunes.library;

import org.w3c.dom.Element;

public interface DictionaryProcessor {
    void handleKeyValue(Element key, Element value);
}
