package com.jrelay.ui.shared.utils.mapper;

import java.util.List;

import com.jrelay.core.models.request.body.BinaryBody;
import com.jrelay.core.models.request.body.Body;
import com.jrelay.core.models.request.body.FormDataBody;
import com.jrelay.core.models.request.body.FormEncodeBody;
import com.jrelay.core.models.request.body.JsonBody;
import com.jrelay.core.models.request.body.PlainTextBody;
import com.jrelay.core.models.request.body.XmlBody;
import com.jrelay.core.models.request.body.FormDataBody.FormDataPart;
import com.jrelay.core.models.request.body.FormEncodeBody.FormEncodePart;

/**
 * Factory class for creating various types of HTTP request bodies.
 * <p>
 * Provides static methods to generate {@link Body} instances for different
 * content types such as JSON, XML, plain text, form-encoded data, multipart
 * form data, and binary files.
 *
 * @author ASDFG14N
 * @since 14-08-2025
 */
public class BodyMapper {

    private BodyMapper() {
    }

    /**
     * Creates a JSON body from the given content string.
     *
     * @param content the JSON content as a string
     * @return a {@link JsonBody} instance containing the provided content
     */
    public static Body json(String content) {
        return new JsonBody(content);
    }

    /**
     * Creates an XML body from the given content string.
     *
     * @param content the XML content as a string
     * @return an {@link XmlBody} instance containing the provided content
     */
    public static Body xml(String content) {
        return new XmlBody(content);
    }

    /**
     * Creates a plain text body from the given content string.
     *
     * @param content the plain text content
     * @return a {@link PlainTextBody} instance containing the provided content
     */
    public static Body plain(String content) {
        return new PlainTextBody(content);
    }

    /**
     * Creates a form-encoded body from the given list of form parts.
     *
     * @param forms a list of {@link FormEncodePart} objects representing the form
     *              fields
     * @return a {@link FormEncodeBody} instance containing the provided form data
     */
    public static Body formEncode(List<FormEncodePart> forms) {
        return new FormEncodeBody(forms);
    }

    /**
     * Creates a multipart form data body from the given list of form parts.
     *
     * @param forms a list of {@link FormDataPart} objects representing the form
     *              fields
     * @return a {@link FormDataBody} instance containing the provided form data
     */
    public static Body formData(List<FormDataPart> forms) {
        return new FormDataBody(forms);
    }

    /**
     * Creates a binary body from the given file path.
     *
     * @param filePath the path to the binary file
     * @return a {@link BinaryBody} instance representing the file content
     */
    public static Body binary(String filePath) {
        return new BinaryBody(filePath);
    }
}