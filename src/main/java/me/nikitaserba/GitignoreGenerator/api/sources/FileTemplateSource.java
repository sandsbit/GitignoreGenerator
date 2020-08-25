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

package me.nikitaserba.GitignoreGenerator.api.sources;

import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;

import java.util.List;

/**
 * .gitignore source that uses templates from "templates" folder in resources.
 *
 * @version 1.0
 */
public class FileTemplateSource implements GitignoreSource {

    // empty constructor is needed for auto source finding using reflection
    public FileTemplateSource() {
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<Source> getAllSources() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<Source> getSourcesByType(TemplateType type) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GitignoreTemplate parse(String source) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GitignoreTemplate parse(Source source) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GitignoreTemplate[] parseAll() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
