/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.shared.logging;

public interface Logger {

    /**
     * Logs a message with the log level of debug
     *
     * @param o
     *         The object to log
     */
    public void debug(Object o);

    /**
     * Logs a message with the log level of info
     *
     * @param o
     *         The object to log
     */
    public void info(Object o);

    /**
     * Logs a message with the log level of warn
     *
     * @param o
     *         The object to log
     */
    public void warn(Object o);

    /**
     * Logs a message with the log level of err
     *
     * @param o
     *         The object to log
     */
    public void error(Object o);
}
