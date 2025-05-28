package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchProvider;

public class OcsSearchProviderMapperTest {

    private OcsSearchProviderMapper mapper;

    @Before
    public void setup() {
        this.mapper = OcsSearchProviderMapper.INSTANCE;
    }

    @Test
    public void toDto() {

        final var entity = new SearchProvider();
        entity.setRemoteId("foo");
        entity.setAppId("bar");
        entity.setName("baz");
        entity.setIcon("qux");
        entity.setOrder(4711);
        entity.setInAppSearch(true);

        final var dto = mapper.toDto(entity);

        assertEquals("foo", dto.remoteId());
        assertEquals("bar", dto.appId());
        assertEquals("baz", dto.name());
        assertEquals("qux", dto.icon());
        assertEquals(4711, dto.order());
        assertTrue(dto.inAppSearch());
    }

    @Test
    public void toEntity() {

        final var dto = new OcsSearchProvider("foo", "bar", "baz", "qux", 4711, true);

        final var entity = mapper.toEntity(dto);

        assertEquals("foo", entity.getRemoteId());
        assertEquals("bar", entity.getAppId());
        assertEquals("baz", entity.getName());
        assertEquals("qux", entity.getIcon());
        assertEquals(4711, entity.getOrder());
        assertTrue(entity.isInAppSearch());
    }
}
