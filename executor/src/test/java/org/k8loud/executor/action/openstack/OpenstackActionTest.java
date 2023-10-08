package org.k8loud.executor.action.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.RESIZE_SERVER_FAILED;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenstackActionTest {
    private final OpenstackException OPENSTACK_EXCEPTION = new OpenstackException("MESSAGE", RESIZE_SERVER_FAILED);

    @Mock
    OpenstackAction openstackAction;

    @Test
    void testThrowOpenstackExceptionDuringPerform() throws OpenstackException {
        // given
        doThrow(OPENSTACK_EXCEPTION).when(openstackAction).performOpenstackAction();
        when(openstackAction.perform()).thenCallRealMethod();

        // when
        Throwable throwable = catchThrowable(() -> openstackAction.performOpenstackAction());

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class).hasMessage("MESSAGE");
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(RESIZE_SERVER_FAILED);

        // when
        ExecutionRS response = openstackAction.perform();

        // then
        assertThat(response.getResult()).isEqualTo(OPENSTACK_EXCEPTION.toString());
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.NOT_OK);
    }

    @Test
    void testSuccessPerform() {
        // given
        when(openstackAction.perform()).thenCallRealMethod();

        // when
        Throwable throwable = catchThrowable(() -> openstackAction.performOpenstackAction());

        // then
        assertThat(throwable).doesNotThrowAnyException();

        // when
        ExecutionRS response = openstackAction.perform();

        // then
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }
}
