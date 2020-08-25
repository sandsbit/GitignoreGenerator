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
 * Gitignore file templates that begin with "_test" will be ignored in release version.
 * They're used in unittests.
 *
 * This class is final to allow java use some optimizations. This
 * tag will be removed if it's needed to inherit any class from
 * this class.
 *
 * @version 1.0
 */
public final class FileTemplateSource implements GitignoreSource {

    public static final String NAME = "File template source";

    // when true class will parse all files, used for unittests
    protected boolean testMode = false;

    // empty constructor is needed for auto source finding using reflection
    public FileTemplateSource() {
    }

    /**
     * Return instance of this source that will process all templates,
     * including test template that begin with "_test".
     *
     * Should be used only in unittests.
     *
     * @return instance of FileTemplateSource class.
     */
    public static FileTemplateSource getUnittestInstance() {
        FileTemplateSource source = new FileTemplateSource();
        source.testMode = false;
        return source;
    }

    @Override
    public String getName() {
        return NAME;
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
