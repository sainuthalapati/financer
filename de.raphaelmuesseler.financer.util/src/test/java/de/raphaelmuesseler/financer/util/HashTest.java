package de.raphaelmuesseler.financer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashTest {
    @Test
    public void testHashWithoutSalt() {
        String plain = "abcdefghijklmnopqrstuvwxyz";
        Assertions.assertEquals(Hash.create(plain),
                "71C480DF93D6AE2F1EFAD1447C66C9525E316218CF51FC8D9ED832F2DAF18B73".toLowerCase());
    }

    @Test
    public void testHashWithSalt() {
        String plain = "abcdefghijklmnopqrstuvwxyz";
        String salt = "zyxwvutsrqponmlkjihgfedcba";
        Assertions.assertEquals(Hash.create(plain, salt),
                "20F1FCDF919164C759825590DF190FED6224D8DAF1BC3B4FC65D9559E1955370".toLowerCase());
    }
}