package org.nypl.simplified.app.catalog;

import org.nypl.simplified.books.core.FeedFacetPseudo;
import org.nypl.simplified.books.core.FeedFacetPseudo.FacetType;
import org.nypl.simplified.stack.ImmutableStack;

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;

public final class CatalogFeedArgumentsLocalBooks implements
  CatalogFeedArgumentsType
{
  private static final long                              serialVersionUID;

  static {
    serialVersionUID = 1L;
  }

  private final FacetType                                facet_type;
  private final OptionType<String>                       search_terms;
  private final String                                   title;
  private final ImmutableStack<CatalogFeedArgumentsType> up_stack;

  public CatalogFeedArgumentsLocalBooks(
    final ImmutableStack<CatalogFeedArgumentsType> in_up_stack,
    final String in_title,
    final FeedFacetPseudo.FacetType in_facet_type,
    final OptionType<String> in_search_terms)
  {
    this.up_stack = NullCheck.notNull(in_up_stack);
    this.title = NullCheck.notNull(in_title);
    this.facet_type = NullCheck.notNull(in_facet_type);
    this.search_terms = NullCheck.notNull(in_search_terms);
  }

  public FacetType getFacetType()
  {
    return this.facet_type;
  }

  public OptionType<String> getSearchTerms()
  {
    return this.search_terms;
  }

  @Override public String getTitle()
  {
    return this.title;
  }

  @Override public ImmutableStack<CatalogFeedArgumentsType> getUpStack()
  {
    return this.up_stack;
  }

  @Override public <A, E extends Exception> A matchArguments(
    final CatalogFeedArgumentsMatcherType<A, E> m)
    throws E
  {
    return m.onFeedArgumentsLocalBooks(this);
  }

  @Override public String toString()
  {
    final StringBuilder b = new StringBuilder();
    b.append("[CatalogFeedArgumentsLocalBooks facet_type=");
    b.append(this.facet_type);
    b.append(" search_terms=");
    b.append(this.search_terms);
    b.append(" title=");
    b.append(this.title);
    b.append("]");
    return NullCheck.notNull(b.toString());
  }
}
