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

import me.nikitaserba.GitignoreGenerator.api.exceptions.TemplateParsingException;
import me.nikitaserba.GitignoreGenerator.api.templates.GitignoreTemplate;
import me.nikitaserba.GitignoreGenerator.api.templates.TemplateType;

import java.util.Set;

/**
 * Gitignore source that parses templates from https://github.com/github/gitignore/.
 *
 * @version 1.0
 */
public class GithubRepoSource implements GitignoreSource {

    public static final String NAME = "Github Gitignore repo source";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<Source> getAllSources() throws TemplateParsingException {
        return null;
    }

    @Override
    public Set<Source> getSourcesByType(TemplateType type) throws TemplateParsingException {
        return null;
    }

    @Override
    public GitignoreTemplate parse(Source source) throws TemplateParsingException {
        return null;
    }

    @Override
    public Set<GitignoreTemplate> parseAll() throws TemplateParsingException {
        return null;
    }
}
