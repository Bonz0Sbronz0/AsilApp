package it.uniba.dib.sms232413.Paziente.PazienteFragment.InfoCentroFragment.utility;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class XMLUtility {
    public static List<String> XMLParsing(String xmlContent, String type) throws IOException, XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(new StringReader(xmlContent));
        List<String> list = new ArrayList<>();
        //inizializzo un event type del parser
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("string-array".equals(parser.getName())) {
                    if (type.equals(parser.getAttributeValue(null, "name"))) {
                        eventType = parser.next();
                        //mi trovo nella sezione specifica
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                                list.add(parser.nextText());
                            }
                            eventType = parser.next();

                            if (eventType == XmlPullParser.END_TAG && "string-array".equals(parser.getName())) {
                                break;
                            }
                        }
                    }
                }
            }
            eventType = parser.next();
        }
        return list;
    }
}
