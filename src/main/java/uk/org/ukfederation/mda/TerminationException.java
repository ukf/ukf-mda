/*
 * Copyright (C) 2011 University of Edinburgh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ukfederation.mda;

import net.shibboleth.metadata.pipeline.StageProcessingException;

/**
 * An exception to be thrown to cause command-line aggregator execution to terminate.
 */
public class TerminationException extends StageProcessingException {

    /** Serial version UID. */
    private static final long serialVersionUID = -4659629508135470618L;

    /** Constructor. */
    public TerminationException() {
    }

    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public TerminationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param wrappedException exception to be wrapped by this one
     */
    public TerminationException(Exception wrappedException) {
        super(wrappedException);
    }

    /**
     * Constructor.
     * 
     * @param message exception message
     * @param wrappedException exception to be wrapped by this one
     */
    public TerminationException(String message, Exception wrappedException) {
        super(message, wrappedException);
    }

}