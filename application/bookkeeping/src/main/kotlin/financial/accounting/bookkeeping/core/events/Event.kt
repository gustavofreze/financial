package financial.accounting.bookkeeping.core.events

import financial.shared.buildingblocks.domainevent.Event as DomainEvent
import financial.shared.eventsourcing.Event as EventSourcing

interface Event : DomainEvent, EventSourcing