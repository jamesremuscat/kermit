package org.muscat.kermit.subscriptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Stores a map from regular expressions designed to match paths, to a set of nicknames subscribed to that regex.
 * @author jrem
 */
public class SubscriptionStore implements Serializable {

  private static final long serialVersionUID = -1812913908662240082L;

  private final Map<String, Set<String>> _subscriptions = new LinkedHashMap<String, Set<String>>();

  private volatile String _storeIdent = null;

  /**
   * Called by factory to create a new volatile store.
   */
  private SubscriptionStore() {
    // nothing to do
  }

  private SubscriptionStore(final String storeIdent) {
    _storeIdent = storeIdent;
  }

  /**
   * Subscribe a nickname to a regex.
   * @param regex
   * @param nick
   */
  public void add(final String regex, final String nick) {

    if (!_subscriptions.containsKey(regex)) {
      _subscriptions.put(regex, new LinkedHashSet<String>());
    }

    _subscriptions.get(regex).add(nick);

    save();
  }

  /**
   * Remove a nickname's subscription to a page, if such a subscription exists. If it does not,
   * then the state after calling this method is identical to the state before.
   * @param regex
   * @param nick
   */
  public void remove(final String regex, final String nick) {
    if (_subscriptions.containsKey(regex)) {
      _subscriptions.get(regex).remove(nick);
    }
    save();
  }

  /**
   * Get the collection of nicknames subscribed to a given page. If there are
   * no subscribers, this returns the empty set.
   * @param pageName
   * @return
   */
  public Collection<String> get(final String pathToMatch) {

    final Set<String> nicksToReturn = new LinkedHashSet<String>();

    for (final Entry<String, Set<String>> subscription : _subscriptions.entrySet()) {
      final String regex = subscription.getKey();
      if (Pattern.matches(regex, pathToMatch)) {
        nicksToReturn.addAll(subscription.getValue());
      }
    }

    return nicksToReturn;
  }

  /**
   * Return the collection of regexes that <code>nick</code> is subscribed to.
   * @param nick
   * @return
   */
  public Collection<String> getAllForUser(final String nick) {
    final Set<String> regexes = new LinkedHashSet<String>();

    for (final Entry<String, Set<String>> entry : _subscriptions.entrySet()) {
      if (entry.getValue().contains(nick)) {
        regexes.add(entry.getKey());
      }
    }
    return regexes;
  }

  private void save() {
    if (_storeIdent != null) {
      ObjectOutputStream out;
      try {
        out = new ObjectOutputStream(new FileOutputStream(_storeIdent + ".ser"));
        out.writeObject(this);
        out.close();
      }
      catch (final IOException e) {
        System.err.println("IOException while trying to write serialized subscription store ." + _storeIdent + ".ser'. Subscriptions may not be stored.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Factory for {@link SubscriptionStore} to allow loading from saved file / creating fresh.
   * @author jrem
   */
  public static class Factory {

    public static SubscriptionStore getVolatileStore() {
      return new SubscriptionStore();
    }

    public static SubscriptionStore getSerializableStore(final String storeIdent) {
      final File source = new File(storeIdent + ".ser");

      if (!source.exists()) {
        return new SubscriptionStore(storeIdent);
      }

      ObjectInputStream in;
      try {
        in = new ObjectInputStream(new FileInputStream(source));
        return (SubscriptionStore) in.readObject();
      }
      catch (final Exception e) {
        System.err.println("Exception while trying to read in a serializable store: " + e.getMessage() + ". Using a volatile store for this session.");
        return new SubscriptionStore();
      }

    }

  }

}
