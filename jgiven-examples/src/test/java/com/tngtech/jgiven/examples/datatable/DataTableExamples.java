package com.tngtech.jgiven.examples.datatable;

import static com.tngtech.jgiven.annotation.Table.HeaderType.VERTICAL;
import static java.util.Arrays.asList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.NamedFormat;
import com.tngtech.jgiven.annotation.NamedFormats;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class DataTableExamples extends SimpleScenarioTest<DataTableExamples.DataTableStage> {

    public static class ToUpperCaseFormatter implements ArgumentFormatter<String> {
        @Override
        public String format( String value, String... args ) {
            if( value == null ) {
                return "";
            }

            return value.toUpperCase();
        }
    }

    @Formatf( value = "(uppercased by custom format annotation) %s" )
    @Format( value = ToUpperCaseFormatter.class )
    @Retention( RetentionPolicy.RUNTIME )
    @Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD } )
    public static @interface UpperCasedCustomFormatAnnotationChain {}

    @Formatf( value = "(quoted by custom format annotation) %s" )
    @Quoted
    @Retention( RetentionPolicy.RUNTIME )
    @Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD } )
    static @interface QuotedCustomFormatAnnotationChain {}

    @NamedFormats( { @NamedFormat( name = "name", format = @Format( value = ToUpperCaseFormatter.class ) ),
        @NamedFormat( name = "email", formatAnnotation = QuotedCustomFormatAnnotationChain.class ) } )
    @Retention( RetentionPolicy.RUNTIME )
    public static @interface TestCustomerFieldFormatSet {}

    static class DataTableStage extends Stage<DataTableStage> {

        public DataTableStage a_list_of_lists_is_used_as_parameter( @Table List<List<String>> table ) {
            return self();
        }

        public DataTableStage a_list_of_lists_is_used_as_parameter_with_column_titles(
                @Table( columnTitles = { "Name", "Email" } ) List<List<String>> table ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters( @Table TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_and_some_fields_are_formatted_using_inline_specified_named_formats(
                @Table( fieldsFormat = {
                    @NamedFormat( name = "name", formatAnnotation = UpperCasedCustomFormatAnnotationChain.class ),
                    @NamedFormat( name = "email" )
                } ) TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_and_some_fields_are_formatted_using_a_predefined_set_of_named_formats(
                @Table( fieldsFormatSetAnnotation = TestCustomerFieldFormatSet.class ) TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL(
                @Table( header = VERTICAL ) TestCustomer... testCustomer ) {
            return self();
        }

        public DataTableStage a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL_and_numbered_columns(
                @Table( header = VERTICAL, numberedColumns = true ) TestCustomer... testCustomer ) {
            return self();
        }

        public void some_action_happens() {

        }

        public void a_single_POJO_is_used_as_parameters( @Table( header = VERTICAL ) TestCustomer testCustomer ) {}

        public void a_list_of_POJOs_with_numbered_rows( @Table( numberedRows = true ) TestCustomer... testCustomer ) {}

        public void a_list_of_POJOs_with_numbered_rows_and_custom_header(
                @Table( numberedRowsHeader = "Counter" ) TestCustomer... testCustomer ) {}

        public void a_two_dimensional_array_with_numbered_rows(
                @Table( numberedRows = true, columnTitles = "t" ) Object[][] testCustomer ) {}

    }

    static class TestCustomer {
        String name;

        @Formatf( value = "(quoted at POJO field level) %s" )
        @Quoted
        String email;

        public TestCustomer( String name, String email ) {
            this.name = name;
            this.email = email;
        }
    }

    @Test
    public void a_list_of_list_can_be_used_as_table_parameter() {
        given().a_list_of_lists_is_used_as_parameter( asList( asList( "Name", "Email" ), asList( "John Doe", "john@doe.com" ),
            asList( "Jane Roe", "jane@roe.com" ) ) );
    }

    @Test
    public void a_list_of_list_can_be_used_as_table_parameter_and_column_titles_can_be_set() {
        given().a_list_of_lists_is_used_as_parameter_with_column_titles(
            asList( asList( "John Doe", "john@doe.com" ), asList( "Jane Roe", "jane@roe.com" ) ) );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_data_tables() {
        given().a_list_of_POJOs_is_used_as_parameters( new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" ) );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_formatted_data_tables() {
        given().a_list_of_POJOs_is_used_as_parameters_and_some_fields_are_formatted_using_inline_specified_named_formats(
            new TestCustomer( "John Doe", "john@doe.com" ), new TestCustomer( "Jane Roe", "jane@roe.com" ) ).and()
            .a_list_of_POJOs_is_used_as_parameters_and_some_fields_are_formatted_using_a_predefined_set_of_named_formats(
                new TestCustomer( "John Doe", "john@doe.com" ), new TestCustomer( "Jane Roe", "jane@roe.com" ) ).and()
            .a_list_of_POJOs_is_used_as_parameters_and_some_fields_are_formatted_using_a_predefined_set_of_named_formats(
                new TestCustomer( "John Doe", null ), new TestCustomer( null, "jane@roe.com" ) )
            ;
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_a_data_table_with_a_vertical_header() {
        given().a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL(
            new TestCustomer( "John Doe", "john@doe.com" ), new TestCustomer( "Jane Roe", "jane@roe.com" ) );
    }

    @Test
    public void a_list_of_POJOs_can_be_represented_as_a_data_table_with_a_vertical_header_and_numbered_columns() {
        given().a_list_of_POJOs_is_used_as_parameters_with_header_type_VERTICAL_and_numbered_columns(
            new TestCustomer( "John Doe", "john@doe.com" ), new TestCustomer( "Jane Roe", "jane@roe.com" ) );
    }

    @Test
    public void a_single_POJO_can_be_represented_as_a_data_table() {
        given().a_single_POJO_is_used_as_parameters( new TestCustomer( "Jane Roe", "jane@roe.com" ) );
    }

    @Test
    public void parameter_tables_can_have_numbered_rows() {
        given().a_list_of_POJOs_with_numbered_rows( new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" ), new TestCustomer( "Lee Smith", "lee@smith.com" ) );
    }

    @Test
    public void parameter_tables_can_have_numbered_rows_with_custom_headers() {
        given().a_list_of_POJOs_with_numbered_rows_and_custom_header( new TestCustomer( "John Doe", "john@doe.com" ),
            new TestCustomer( "Jane Roe", "jane@roe.com" ), new TestCustomer( "Lee Smith", "lee@smith.com" ) );
    }

    @Test
    public void two_dimensional_arrays_can_be_numbered() {
        given().a_two_dimensional_array_with_numbered_rows( new Object[][] { { "a" }, { "b" } } );
    }
}
