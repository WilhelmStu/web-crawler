package aau.cc.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HeadingTest {
    private static final Heading HEADING_1 = new Heading("Heading 1", 1);
    private static final Heading HEADING_2 = new Heading("Heading 1", 1);
    private static final Heading HEADING_3 = new Heading("Heading 2");

    @Test
    public void testGetHeadingsTextsAsList() {
        List<Heading> headings = new ArrayList<>();
        headings.add(HEADING_1);
        List<String> result =  Heading.getHeadingsTextsAsList(headings);
        assertEquals(1, result.size());
        assertEquals(HEADING_1.getText(), result.get(0));
    }

    @Test
    public void testEquality() {
        assertEquals(HEADING_1, HEADING_2);
        assertNotEquals(HEADING_1, HEADING_3);
    }

    @Test
    public void testHashCode() {
        assertEquals(HEADING_1.hashCode(), HEADING_2.hashCode());
        assertNotEquals(HEADING_1.hashCode(), HEADING_3.hashCode());
    }
}
