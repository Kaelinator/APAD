package com.kaelkirk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
  /**
   * Rigorous Test :-)
   */
  @Test
  public void shouldAnswerWithTrue()
  {
    assertTrue( true );
  }

  @Test
  public void regexExperiment() {
    Pattern pingPattern = Pattern.compile("^PING :(.*)$", Pattern.MULTILINE);

    String d = "PING :tmi.twitch.tv";
    Matcher pingMatch = pingPattern.matcher(d);

    assertTrue(pingMatch.find());
    assertEquals(pingMatch.groupCount(), 1);
    assertEquals(pingMatch.group(0), d);
    assertEquals(pingMatch.group(1), "tmi.twitch.tv");
  }

  @Test
  public void regexExperimentMessage() {
    Pattern messagePattern = Pattern.compile("^:(\\w+)!.*PRIVMSG #kaelinator17 :(.*)$", Pattern.MULTILINE);

    String d = ":apadinator!apadinator@apadinator.tmi.twitch.tv PRIVMSG #kaelinator17 :hello, I am chat bot";
    Matcher messageMatch = messagePattern.matcher(d);

    assertTrue(messageMatch.find());
    assertEquals(messageMatch.groupCount(), 2);
    assertEquals(messageMatch.group(0), d);
    assertEquals(messageMatch.group(1), "apadinator");
    assertEquals(messageMatch.group(2), "hello, I am chat bot");
    assertTrue(true);
  }
}
