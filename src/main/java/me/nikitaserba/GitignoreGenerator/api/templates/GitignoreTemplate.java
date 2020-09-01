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

import me.nikitaserba.GitignoreGenerator.api.sources.GitignoreSource;

/**
 * Class that stores information about .gitignore template:
 * text, source, type e.g.
 *
 * This class is read-only
 *
 * This class is final to allow java use some optimizations. This
 * tag will be removed if it's needed to inherit any class from
 * this class.
 *
 * @version 1.0
 */
public final class GitignoreTemplate {

    protected String name; // e.g. "Windows", "C/C++" or "JetBrains"
    protected TemplateType type;
    protected String sourceData;  // data source which was used GitignoreSource class (url, file path, etc.)

    protected String gitignoreContent;

    public GitignoreTemplate(String name, TemplateType type,
                             String sourceData, String gitignoreContent) {
        this.name = name;
        this.type = type;
        this.sourceData = sourceData;
        this.gitignoreContent = gitignoreContent;
    }

    public String getName() {
        return name;
    }

    public TemplateType getType() {
        return type;
    }

    public String getSourceData() {
        return sourceData;
    }

    public String getContent() {
        return gitignoreContent;
    }
}
