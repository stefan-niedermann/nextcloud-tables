package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.model.Version;

public class OcsVersionMapperTest {

    private OcsVersionMapper mapper;

    @Before
    public void setup() {
        this.mapper = OcsVersionMapper.INSTANCE;
    }

    @Test
    public void toDto() {

        final var entity = new Version("1.2.3-foo", 1, 2, 3);

        final var dto = mapper.toDto(entity);

        assertEquals(1, dto.major);
        assertEquals(2, dto.minor);
        assertEquals(3, dto.macro);
        assertEquals("1.2.3-foo", dto.string);
        assertNull(dto.edition);
        assertFalse(dto.extendedSupport);
    }

    @Test
    public void toEntity() {

        final var dto = new OcsCapabilitiesResponse.OcsVersion();
        dto.major = 1;
        dto.minor = 2;
        dto.macro = 3;
        dto.string = "1.2.3-foo";

        final var entity = mapper.toEntity(dto);

        assertEquals(1, entity.getMajor());
        assertEquals(2, entity.getMinor());
        assertEquals(3, entity.getPatch());
        assertEquals("1.2.3-foo", entity.getVersion());
    }
}
