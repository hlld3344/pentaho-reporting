/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A crosstab group represents the page, row, column and detail sections of a cube. The other axises are handled as
 * regular relational groups.
 * <p/>
 * The group's header and footer can be used to print page-axis header and footer sections for the crosstab, but they
 * will not be used as bands for the tab-header and -footers. The crosstabs column and row headers are autogenerated and
 * part of the group-body.
 *
 * @author Thomas Morgner
 */
public class CrosstabGroup extends Group {
  private static final String[] EMPTY_FIELDS = new String[0];

  private GroupHeader header;
  private GroupFooter footer;
  private NoDataBand noDataBand;

  public CrosstabGroup() {
    init();
  }

  public CrosstabGroup( final GroupBody body ) {
    super( body );
    validateBody( body );
    init();
  }

  public CrosstabGroup( final CrosstabRowGroupBody body ) {
    super( body );

    init();
  }

  public CrosstabGroup( final CrosstabOtherGroupBody body ) {
    super( body );

    init();
  }

  private void init() {
    setElementType( new CrosstabGroupType() );

    this.footer = new GroupFooter();
    this.header = new GroupHeader();
    this.noDataBand = new NoDataBand();

    registerAsChild( footer );
    registerAsChild( header );
    registerAsChild( noDataBand );
  }

  /**
   * Returns the group header.
   * <P>
   * The group header is a report band that contains elements that should be printed at the start of a group.
   *
   * @return the group header.
   */
  public GroupHeader getHeader() {
    return header;
  }

  /**
   * Sets the header for the group.
   *
   * @param header
   *          the header (null not permitted).
   * @throws NullPointerException
   *           if the given header is null
   */
  public void setHeader( final GroupHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "Header must not be null" );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element element = this.header;
    this.header.setParent( null );
    this.header = header;
    this.header.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.header );
  }

  /**
   * Returns the group footer.
   *
   * @return the footer.
   */
  public GroupFooter getFooter() {
    return footer;
  }

  /**
   * Sets the footer for the group.
   *
   * @param footer
   *          the footer (null not permitted).
   * @throws NullPointerException
   *           if the given footer is null.
   */
  public void setFooter( final GroupFooter footer ) {
    if ( footer == null ) {
      throw new NullPointerException( "The footer must not be null" );
    }
    validateLooping( footer );
    if ( unregisterParent( footer ) ) {
      return;
    }

    final Element element = this.footer;
    this.footer.setParent( null );
    this.footer = footer;
    this.footer.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.footer );
  }

  public NoDataBand getNoDataBand() {
    return noDataBand;
  }

  public void setNoDataBand( final NoDataBand noDataBand ) {
    if ( noDataBand == null ) {
      throw new NullPointerException( "The noDataBand must not be null" );
    }
    validateLooping( noDataBand );
    if ( unregisterParent( noDataBand ) ) {
      return;
    }
    final NoDataBand oldElement = this.noDataBand;
    this.noDataBand.setParent( null );
    this.noDataBand = noDataBand;
    this.noDataBand.setParent( this );

    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( this.noDataBand );
  }

  protected GroupBody createDefaultBody() {
    return new CrosstabRowGroupBody();
  }

  public boolean isGroupChange( final DataRow dataRow ) {
    // always false. We do only return if one of the parent groups claims that there is a group change.
    return false;
  }

  public void setBody( final GroupBody body ) {
    validateBody( body );
    super.setBody( body );
  }

  private void validateBody( final GroupBody body ) {
    if ( body instanceof CrosstabRowGroupBody == false && body instanceof CrosstabOtherGroupBody == false ) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   */
  public CrosstabGroup clone() {
    final CrosstabGroup g = (CrosstabGroup) super.clone();
    g.footer = (GroupFooter) footer.clone();
    g.header = (GroupHeader) header.clone();
    g.noDataBand = (NoDataBand) noDataBand.clone();

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    g.registerAsChild( g.noDataBand );
    return g;
  }

  public CrosstabGroup derive( final boolean preserveElementInstanceIds ) {
    final CrosstabGroup g = (CrosstabGroup) super.derive( preserveElementInstanceIds );
    g.footer = (GroupFooter) footer.derive( preserveElementInstanceIds );
    g.header = (GroupHeader) header.derive( preserveElementInstanceIds );
    g.noDataBand = (NoDataBand) noDataBand.derive( preserveElementInstanceIds );

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    g.registerAsChild( g.noDataBand );
    return g;
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( footer == element ) {
      this.footer.setParent( null );
      this.footer = new GroupFooter();
      this.footer.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.footer );

    } else if ( header == element ) {
      this.header.setParent( null );
      this.header = new GroupHeader();
      this.header.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.header );
    } else if ( noDataBand == element ) {
      this.noDataBand.setParent( null );
      this.noDataBand = new NoDataBand();
      this.noDataBand.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.noDataBand );
    } else {
      super.removeElement( element );
    }
    // Else: Ignore the request, none of my childs.
  }

  public int getElementCount() {
    return 4;
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return header;
      case 1:
        return noDataBand;
      case 2:
        return getBody();
      case 3:
        return footer;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt( final int index, final Element element ) {
    switch ( index ) {
      case 0:
        setHeader( (GroupHeader) element );
        break;
      case 1:
        setNoDataBand( (NoDataBand) element );
        break;
      case 2:
        setBody( (GroupBody) element );
        break;
      case 3:
        setFooter( (GroupFooter) element );
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setDetailsMode( final CrosstabDetailMode mode ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.DETAIL_MODE, mode );
  }

  public CrosstabDetailMode getDetailsMode() {
    return (CrosstabDetailMode) getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.DETAIL_MODE );
  }

  public Boolean getPrintDetailsHeader() {
    return (Boolean) getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_DETAIL_HEADER );
  }

  public void setPrintDetailsHeader( final Boolean printDetailsHeader ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_DETAIL_HEADER, printDetailsHeader );
  }

  public Boolean getPrintColumnTitleHeader() {
    return (Boolean) getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_COLUMN_TITLE_HEADER );
  }

  public void setPrintColumnTitleHeader( final Boolean printColumnTitleHeader ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_COLUMN_TITLE_HEADER,
        printColumnTitleHeader );
  }

  /**
   * Sets the fields for this group. The given list must contain Strings defining the needed fields from the DataRow.
   * Don't reference Function-Fields here, functions are not supported in th groupfield definition.
   *
   * @param c
   *          the list containing strings.
   * @throws NullPointerException
   *           if the given list is null or the list contains null-values.
   */
  public void setPaddingFields( final List<String> c ) {
    if ( c == null ) {
      throw new NullPointerException();
    }
    final String[] fields = c.toArray( new String[c.size()] );
    setPaddingFieldsArray( fields );
  }

  public void clearPaddingFields() {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PADDING_FIELDS, EMPTY_FIELDS );
  }

  /**
   * Adds a field to the group. The field names must correspond to the column names in the report's TableModel.
   *
   * @param name
   *          the field name (null not permitted).
   * @throws NullPointerException
   *           if the name is null
   */
  public void addPaddingField( final String name ) {
    if ( name == null ) {
      throw new NullPointerException( "Group.addField(...): name is null." );
    }
    final ArrayList<String> fieldsList = new ArrayList<String>( getPaddingFields() );
    fieldsList.add( name );
    Collections.sort( fieldsList );
    setPaddingFieldsArray( fieldsList.toArray( new String[fieldsList.size()] ) );
  }

  /**
   * Returns the list of fields for this group.
   *
   * @return a list (unmodifiable) of fields for the group.
   */
  public List<String> getPaddingFields() {
    return Collections.unmodifiableList( Arrays.asList( getPaddingFieldsArray() ) );
  }

  public void setPaddingFieldsArray( final String[] fields ) {
    if ( fields == null ) {
      throw new NullPointerException();
    }
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PADDING_FIELDS, fields.clone() );
  }

  /**
   * Returns the group fields as array.
   *
   * @return the fields as string array.
   */
  public String[] getPaddingFieldsArray() {
    final Object o = getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PADDING_FIELDS );
    if ( o instanceof String[] ) {
      final String[] fields = (String[]) o;
      return fields.clone();
    }
    return EMPTY_FIELDS;
  }

  public List<SortConstraint> getSortingConstraint() {
    return mapFields( getPaddingFields() );
  }
}
