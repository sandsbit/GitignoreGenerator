/*
 * GitignoreGenerator
 * https://github.com/sandsbit/GitignoreGenerator
 *
 * Copyright (C) 2020 Nikita Serba. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.nikitaserba.GitignoreGenerator.api.templates;

/**
 * Possible .gitignore templates categories.
 *
 * @version 1.0
 */
public enum TemplateType {
    OS,  // .gitignore used for projects that are developed in a certain OS (e.g. Windows or macOS)
    PROGRAMMING_LANGUAGE,  // .gitignore for certain programming language
    LIBRARY,  // .gitignore used with projects that use certain library (e.g. Qt)
    UTILITY,  // .gitignore used when certain utility (e.g. compiler) is used in project
    IDE,  // .gitignore used when projects is developed in certain IDE
    OTHER
}
