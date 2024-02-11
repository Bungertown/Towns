CREATE TABLE "${tablePrefix}resident" (
    "id"          UUID                             NOT NULL, -- The unique ID of the resident
    "name"        TEXT                             NOT NULL, -- The name of the resident
    "created"     TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL, -- The date the resident was created
    "last_joined" TIMESTAMP DEFAULT NULL,                    -- The date the resident last joined, if at all
    "town_id"     INT4      DEFAULT NULL,                    -- The town the resident is a member of
    "metadata"    JSON      DEFAULT JSON '{}'      NOT NULL, -- Any extra metadata about the resident
    CONSTRAINT "${tablePrefix}resident_pk"
        PRIMARY KEY ("id")
);

CREATE INDEX "${tablePrefix}resident_name_index"
    ON "${tablePrefix}resident" ("name");

CREATE TABLE "${tablePrefix}town" (
    "id"          INT4 auto_increment,                        -- The unique ID of the town
    "name"        TEXT                              NOT NULL  -- The name of the town
        CONSTRAINT "${tablePrefix}town_name_uindex"
            UNIQUE,
    "owner_id"    UUID                              NOT NULL, -- The resident that owns the town
    "created"     TIMESTAMP DEFAULT LOCALTIMESTAMP  NOT NULL, -- The date the town was created
    "open"        BOOL      DEFAULT FALSE           NOT NULL, -- Whether the town is open to new residents
    "public"      BOOL      DEFAULT FALSE           NOT NULL, -- Whether the town's spawn can be teleported to by anyone
    "slogan"      TEXT      DEFAULT NULL,                     -- The town's slogan
    "metadata"    JSON      DEFAULT '{}'            NOT NULL, -- Any extra metadata about the town
    CONSTRAINT "${tablePrefix}town_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "${tablePrefix}town_owner_id_fk"
        FOREIGN KEY ("owner_id") REFERENCES "${tablePrefix}resident"
            ON DELETE RESTRICT
);

alter table "${tablePrefix}resident"
    add CONSTRAINT "${tablePrefix}resident_town_id_fk"
        FOREIGN KEY ("town_id") REFERENCES "${tablePrefix}town"
            ON DELETE SET NULL;

CREATE TABLE "${tablePrefix}world" (
    "id"             UUID               NOT NULL, -- The unique ID of the world
    "name"           TEXT               NOT NULL, -- The name of the world
    "plugin_enabled" BOOL DEFAULT FALSE NOT NULL, -- Whether the plugin is enabled in this world
    "pvp"            BOOL DEFAULT FALSE NOT NULL, -- Whether PVP is enabled in this world
    "pvp_forced"     BOOL DEFAULT FALSE NOT NULL, -- Whether PVP is force enabled in this world
    "friendly_fire"  BOOL DEFAULT FALSE NOT NULL, -- Whether friendly fire is enabled in this world
    CONSTRAINT "${tablePrefix}world_pk"
        PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "${tablePrefix}world_name_uindex"
    ON "${tablePrefix}world" ("name");

CREATE TABLE "${tablePrefix}plot" (
    "id"            INT8 AUTO_INCREMENT,          -- The unique ID of the plot
    "world"         UUID                NOT NULL, -- The world the plot is in
    "chunk_x"       INT4                NOT NULL, -- The X coordinate of the chunk
    "chunk_z"       INT4                NOT NULL, -- The Z coordinate of the chunk
    "town_id"       INT4                NOT NULL, -- The town that owns this plot
    "resident_id"   UUID DEFAULT NULL,            -- The resident that owns this plot, if any
    "outpost"       BOOL DEFAULT FALSE  NOT NULL, -- Whether this plot is an outpost
    CONSTRAINT "${tablePrefix}plot_pk"
        PRIMARY KEY ("id"),
    CONSTRAINT "${tablePrefix}plot_town_id_fk"
        FOREIGN KEY ("town_id") REFERENCES "${tablePrefix}town"
            ON DELETE CASCADE,
    CONSTRAINT "${tablePrefix}plot_resident_id_fk"
        FOREIGN KEY ("resident_id") REFERENCES "${tablePrefix}resident"
            ON DELETE SET NULL
);

CREATE UNIQUE INDEX "${tablePrefix}plot_world_chunk_x_chunk_z_uindex"
    ON "${tablePrefix}plot" ("world", "chunk_x", "chunk_z");